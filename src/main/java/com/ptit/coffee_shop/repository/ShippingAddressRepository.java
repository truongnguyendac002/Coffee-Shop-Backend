package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    @Query("SELECT sa FROM ShippingAddress sa WHERE sa.user.id = :userId and sa.status = :status")
    List<ShippingAddress> findByUserId(@Param("userId") Long userId, @Param("status") Status status);
}
