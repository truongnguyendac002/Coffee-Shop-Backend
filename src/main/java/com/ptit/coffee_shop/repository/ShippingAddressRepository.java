package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.ShippingAddress;
import com.ptit.coffee_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByUser(User user);
}
