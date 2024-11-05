package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

//    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
//    List<OrderItem> findByOrderId(@Param("orderId") int orderId);
}
