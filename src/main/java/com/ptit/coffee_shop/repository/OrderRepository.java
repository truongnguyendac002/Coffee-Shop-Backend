package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT od FROM Order od WHERE od.shippingAddress.id = :shippingAddressId")
    Optional<Order> findByShippingAddressId(@Param("shippingAddressId") Long shippingAddressId);
}
