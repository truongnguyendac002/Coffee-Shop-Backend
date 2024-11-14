package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.model.ProductItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderItemRequest {
    @JsonProperty("ProductItemId")
    private long productItemId;

    @JsonProperty("Amount")
    private int amount;

    @JsonProperty("Price")
    private double price;

    @JsonProperty("Discount")
    private double discount;

}
