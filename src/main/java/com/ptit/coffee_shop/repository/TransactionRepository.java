package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
