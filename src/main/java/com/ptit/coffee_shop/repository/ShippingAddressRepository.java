package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
}
