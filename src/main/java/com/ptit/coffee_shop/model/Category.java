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
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "Description")
    private String description;

    @Column(name = "default_image_url")
    private String defaultImageUrl;

    @PrePersist
    public void prePersist() {
        if (status == null) status = Status.ACTIVE;
    }
}
