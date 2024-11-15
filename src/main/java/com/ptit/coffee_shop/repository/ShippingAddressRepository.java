package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.ShippingAddress;
import com.ptit.coffee_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    Optional<List<ShippingAddress>> findByUser(User user);
}
