package com.ptit.coffee_shop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptit.coffee_shop.common.enums.OrderStatus;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.payload.response.OrderItemResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "price")
    private double price;

    @Column(name = "discount")
    private double discount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "is_reviewed")
    private boolean isReviewed;

    public OrderItemResponse toResponse() {
        return new OrderItemResponse(id, productItem.getId(), productItem.getProduct().getName(), productItem.getType().getName(), amount, price, discount, isReviewed);
    }

    @PrePersist
    public void prePersist() {
        isReviewed = false;
    }
}
