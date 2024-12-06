package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticResponse {
    private long productId;
    private String productName;
    private String categoryName;
    private String brandName;
    private long quantitySold;
    private double totalRevenue;
}
