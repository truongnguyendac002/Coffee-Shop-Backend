package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FavoriteProductRequest {

    @JsonProperty("ProductId")
    private long productId;

    @JsonProperty("UserId")
    private long userId;
}
