package com.ptit.coffee_shop.payload.response;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.model.TypeProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponse {
    private long id;
    private double price;
    private int stock;
    private double discount;
    private ProductResponse productResponse;
    private TypeProduct type;
    private Status status;
}
