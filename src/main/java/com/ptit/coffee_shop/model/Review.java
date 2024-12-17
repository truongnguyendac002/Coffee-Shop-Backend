package com.ptit.coffee_shop.model;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.payload.response.ReviewResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rating")
    private double rating;

    @Column(name = "comment")
    private String comment;

    @OneToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void prePersist() {
        createAt = new Date();
        if( status == null ) status = Status.ACTIVE;
    }

    public ReviewResponse toResponse() {
        return new ReviewResponse(id, orderItem.getOrder().getShippingAddress().getUser().getEmail(), orderItem.getOrder().getShippingAddress().getUser().getId() , orderItem.getOrder().getShippingAddress().getUser().getName(),
                                    orderItem.getOrder().getShippingAddress().getUser().getProfile_img(), rating, comment, createAt);
    }
}
