package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.common.enums.OrderStatus;
import com.ptit.coffee_shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT od FROM Order od WHERE od.shippingAddress.id = :shippingAddressId")
    Optional<Order> findByShippingAddressId(@Param("shippingAddressId") Long shippingAddressId);

    @Query("SELECT od FROM Order od WHERE od.shippingAddress.user.id = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);


    @Query("SELECT od FROM Order od WHERE od.status = :orderStatus")
    List<Order> findByStatus(@Param("orderStatus") OrderStatus orderStatus );
}
