package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = ?")
    List<OrderItem> findByOrderId(int orderId);
}
