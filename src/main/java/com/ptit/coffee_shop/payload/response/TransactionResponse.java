package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionResponse {
    private long transactionId;
    private String transactionNo;
    private String txnRef;
    private Date payDate;
    private double amount;
    private String command;
    private long orderId;
}
