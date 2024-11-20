package com.ptit.coffee_shop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor

@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "txn_ref")
    private String txnRef;

    @Column(name = "pay_date")
    private Date payDate;

    @Column(name = "amount")
    private double amount;

    @Column(name = "command")
    private String command;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
