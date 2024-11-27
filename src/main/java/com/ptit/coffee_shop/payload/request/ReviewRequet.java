package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequet {
    @JsonProperty("Rating")
    private double rating;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("OrderItemId")
    private long orderItemId;
}
