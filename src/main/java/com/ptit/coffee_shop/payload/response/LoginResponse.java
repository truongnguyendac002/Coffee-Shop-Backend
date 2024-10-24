package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String respCode;
    private String respMessage;
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private int refreshExpiresIn;
    private final String tokenType = "Bearer";
}
