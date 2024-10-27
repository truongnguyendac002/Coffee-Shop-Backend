package com.ptit.coffee_shop.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class JwtAPIException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public JwtAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

}
