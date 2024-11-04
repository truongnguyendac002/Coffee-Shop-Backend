package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.model.OrderItem;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private MessageBuilder messageBuilder;

    public RespMessage getAllOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        return messageBuilder.buildSuccessMessage(orderItems);
    }
}
