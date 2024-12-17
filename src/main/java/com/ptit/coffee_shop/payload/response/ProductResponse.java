package com.ptit.coffee_shop.payload.response;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.model.Brand;
import com.ptit.coffee_shop.model.Category;
import com.ptit.coffee_shop.model.Image;
import com.ptit.coffee_shop.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private long id;

    private String name;

    private String description;

    private Category category;

    private Brand brand;

    private Status status;

    private BigDecimal price;

    private List<Image> images;

    private double rating;

    private int totalReview;

    private int totalSold;

    private double maxPrice;

    private double minPrice;


}
