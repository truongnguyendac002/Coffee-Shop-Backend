package com.ptit.coffee_shop.payload.response;

import com.ptit.coffee_shop.model.Order;
import com.ptit.coffee_shop.model.ProductItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private long id;

    private int amount;

    private double price;

    private double discount;

    private ProductItem productItem;

    private Order order;

    private boolean isReviewed;
}
