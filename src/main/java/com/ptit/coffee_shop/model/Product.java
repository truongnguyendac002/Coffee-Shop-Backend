package com.ptit.coffee_shop.model;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.payload.response.ProductResponse;
import com.ptit.coffee_shop.payload.response.ProductStatisticResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne()
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void prePersist() {
        if (status == null) status = Status.ACTIVE;
    }

    public ProductStatisticResponse toStatisticResponse() {
        return new ProductStatisticResponse(id, name, category.getName(), brand.getName(), 0, 0);
    }
}
