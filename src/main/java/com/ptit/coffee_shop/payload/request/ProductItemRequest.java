package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductItemRequest {
    @JsonProperty("Price")
    private double price;
    @JsonProperty("Stock")
    private int stock;
    @JsonProperty("Discount")
    private double discount;
    @JsonProperty("ProductId")
    private long productId;
    @JsonProperty("TypeId")
    private long typeId;
}
