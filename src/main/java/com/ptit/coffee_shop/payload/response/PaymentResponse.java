package com.ptit.coffee_shop.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {

    private String status;
    private String message;
    private String URL;
}
