package com.ptit.coffee_shop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.payload.response.ShippingAddressResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shipping_address")
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        if (status == null) status = Status.ACTIVE;
    }


    public ShippingAddressResponse toResponse() {
        return new ShippingAddressResponse(id, receiverName, receiverPhone, location, status, user.getId());
    }
}
