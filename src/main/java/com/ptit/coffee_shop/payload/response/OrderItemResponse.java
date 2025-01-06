package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class OrderItemResponse {
    private long orderItemId;
    private long productItemId;
    private long productId;
    private String productName;
    private String productType;
    private String productImage;
    private int amount;
    private double price;
    private double discount;
    private boolean isReviewed;
}
