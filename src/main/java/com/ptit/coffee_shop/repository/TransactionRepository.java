package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT ts FROM Transaction ts WHERE ts.order.id = :orderId")
    Optional<Transaction> findByOrderId(@Param("orderId") Long orderId);

}
