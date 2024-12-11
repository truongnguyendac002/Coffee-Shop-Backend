package com.ptit.coffee_shop.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProductResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("product")
    private ProductResponse productResponse;

    @JsonProperty("user_id")
    private Long userId;
}
