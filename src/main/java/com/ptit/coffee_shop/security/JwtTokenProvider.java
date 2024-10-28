package com.ptit.coffee_shop.security;

import com.ptit.coffee_shop.common.enums.RoleEnum;
import com.ptit.coffee_shop.exception.JwtAPIException;
import com.ptit.coffee_shop.payload.response.LoginResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.access-jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.refresh-jwt-expiration-milliseconds}")
    private long jwtRefreshExpirationDate;


    public LoginResponse generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
        String accessToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();

        Date refreshExpireDate = new Date(currentDate.getTime() + jwtRefreshExpirationDate);
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(refreshExpireDate)
                .signWith(key())
                .compact();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresIn((int) jwtExpirationDate)
                .refreshToken(refreshToken)
                .refreshExpiresIn((int) jwtRefreshExpirationDate)
                .build();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new JwtAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtAPIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
        }
    }
}
