package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItemRequest {
    @JsonProperty("Quantity")
    private Integer quantity;

    @JsonProperty("ProductItemId")
    private Long productItemId;
}
