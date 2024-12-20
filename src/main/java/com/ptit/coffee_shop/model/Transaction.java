package com.ptit.coffee_shop.model;

import com.ptit.coffee_shop.payload.response.TransactionResponse;
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

    public TransactionResponse toTransactionResponse() {
        return TransactionResponse.builder()
                .transactionId(id)
                .transactionNo(transactionNo)
                .txnRef(txnRef)
                .payDate(payDate)
                .amount(amount)
                .command(command)
                .orderId(order.getId())
                .build();
    }
}
