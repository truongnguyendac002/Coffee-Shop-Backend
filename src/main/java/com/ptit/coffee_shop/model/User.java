package com.ptit.coffee_shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.payload.response.ProfileResponse;
import com.ptit.coffee_shop.payload.response.UserStatisticResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne()
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "updated_at")
    private Date updated_at;

    @Column(name= "phone")
    private String phone;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "profile_img")
    private String profile_img;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

    @PrePersist
    public void prePersist() {
        created_at = new Date();
        if (updated_at == null) updated_at = created_at;
    }

    @PreUpdate
    public void preUpdate() {
        updated_at = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role != null ? role.getAuthorities() : Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public boolean isEnabled() {
        return status.equals(Status.ACTIVE);
    }

    public ProfileResponse toProfileResponse() {
        return ProfileResponse.builder()
                .id(id)
                .email(email)
                .name(name)
                .phone(phone)
                .profile_img(profile_img)
                .created_at(created_at)
                .build();
    }

    public UserStatisticResponse toStatistic() {
        return new UserStatisticResponse(id, name, email, created_at, 0);
    }
}
