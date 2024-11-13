package com.ptit.coffee_shop.security;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.payload.response.RespMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public final MessageBuilder messageBuilder;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        RespMessage respMessage = request.getAttribute("exception") != null ? (RespMessage) request.getAttribute("exception") : messageBuilder.buildFailureMessage(Constant.UNAUTHORIZED, null, "Authentication error: " + authException);
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(GsonUtil.getInstance().toJson(respMessage));
    }
}
