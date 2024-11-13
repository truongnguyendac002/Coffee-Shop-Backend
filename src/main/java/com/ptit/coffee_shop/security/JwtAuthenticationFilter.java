package com.ptit.coffee_shop.security;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.response.RespMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final MessageBuilder messageBuilder;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        log.info("Token: {}", token);
        try {
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                log.info("Username: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), null, e.getMessage());
            request.setAttribute("exception", respMessage);
        }
        catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            request.setAttribute("exception", respMessage);
        }
        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("authorization");
        log.info("Bearer Token: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        else {
            return null;
        }
    }
}
