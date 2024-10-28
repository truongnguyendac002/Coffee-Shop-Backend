package com.ptit.coffee_shop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name= "phone")
    private String phone;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "profile_img")
    private String profile_img;

    @PrePersist
    public void prePersist() {
        created_at = new Date();
    }
}
