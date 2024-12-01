package com.ptit.coffee_shop.payload.response;

import com.ptit.coffee_shop.model.Order;
import com.ptit.coffee_shop.model.ProductItem;
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
    private String productName;
    private String productType;
    private int amount;
    private double price;
    private double discount;

//    private ProductItem productItem;

//    private Order order;

    private boolean isReviewed;
}
