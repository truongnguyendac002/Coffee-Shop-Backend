package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemRequest {
    @JsonProperty("Quantity")
    private int quantity;

    @JsonProperty("ProductItemId")
    private long productItemId;

    @JsonProperty("UserId")
    private long userId;
}
