package com.ptit.coffee_shop.model;

import com.ptit.coffee_shop.common.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_item")
public class ProductItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price")
    private double price;

    @Column(name = "stock")
    private int stock;

    @Column(name = "discount")
    private double discount;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "type_id")
    private TypeProduct type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void prePersist() {
        if (status == null) status = Status.ACTIVE;
    }
}
