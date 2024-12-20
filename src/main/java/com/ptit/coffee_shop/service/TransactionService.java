package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Order;
import com.ptit.coffee_shop.model.Transaction;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.TransactionRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.OrderRepository;
import com.ptit.coffee_shop.repository.TransactionRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MessageBuilder messageBuilder;

    public RespMessage addTransaction(TransactionRequest transactionRequest) {
        Optional<Order> orderOptional = orderRepository.findById(transactionRequest.getOrderId());
        if (orderOptional.isPresent()) {
            Transaction transaction = new Transaction();
            transaction.setOrder(orderOptional.get());
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setTransactionNo(transactionRequest.getTransactionNo());
            transaction.setCommand("pay");
            transaction.setPayDate(transactionRequest.getPayDate());
            transaction.setTxnRef(transactionRequest.getTxnRef());
            try {
                transactionRepository.save(transaction);
                return messageBuilder.buildSuccessMessage(transaction.toTransactionResponse());
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.UNDEFINED, null, "Transaction could not be saved");
            }
        } else {
            throw new CoffeeShopException(Constant.NOT_FOUND, null, "Order not found");
        }
    }

    public RespMessage getTransaction(long orderId) {
        Optional<Transaction> transactionOptional = transactionRepository.findByOrderId(orderId);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            return messageBuilder.buildSuccessMessage(transaction.toTransactionResponse());
        }
        throw new CoffeeShopException(Constant.NOT_FOUND, null, "Transaction not found");
    }
}
