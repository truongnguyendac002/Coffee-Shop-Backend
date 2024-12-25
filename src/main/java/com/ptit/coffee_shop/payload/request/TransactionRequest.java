package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @JsonProperty("TransactionNo")
    private String transactionNo;

    @JsonProperty("TxnRef")
    private String txnRef;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("PayDate")
    private Date payDate;

    @JsonProperty("Amount")
    private Double amount;

    @JsonProperty("OrderId")
    private long orderId;

}
