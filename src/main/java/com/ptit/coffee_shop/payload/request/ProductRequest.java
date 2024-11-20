package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("CategoryId")
    private long categoryId;
    @JsonProperty("BrandId")
    private long brandId;
    @JsonProperty("Price")
    private BigDecimal price = BigDecimal.valueOf(50.00); // Giá mặc định là 50$
}
