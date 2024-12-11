package com.ptit.coffee_shop.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.model.ProductItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("product_item")
    private ProductItemResponse productItemResponse;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("user_id")
    private Long userId;

}
