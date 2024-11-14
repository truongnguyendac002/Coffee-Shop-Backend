package com.ptit.coffee_shop.model;

import com.ptit.coffee_shop.common.enums.Status;
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

    @Column(name = "reciever_name")
    private String recieverName;

    @Column(name = "reciever_phone")
    private String recieverPhone;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private Status status;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        if (status == null) status = Status.ACTIVE;
    }
}
