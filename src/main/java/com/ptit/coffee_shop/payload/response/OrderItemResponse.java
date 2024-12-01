package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderItemResponse {
    private long orderItemId;
    private long productItemId;
    private String productName;
    private String productType;
    private int amount;
    private double price;
    private double discount;
}
