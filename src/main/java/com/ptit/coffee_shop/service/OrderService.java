package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.OrderStatus;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Order;
import com.ptit.coffee_shop.model.OrderItem;
import com.ptit.coffee_shop.model.ProductItem;
import com.ptit.coffee_shop.model.ShippingAddress;
import com.ptit.coffee_shop.payload.request.OrderItemRequest;
import com.ptit.coffee_shop.payload.request.OrderRequest;
import com.ptit.coffee_shop.payload.response.OrderResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.OrderItemRepository;
import com.ptit.coffee_shop.repository.OrderRepository;
import com.ptit.coffee_shop.repository.ProductItemRepository;
import com.ptit.coffee_shop.repository.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MessageBuilder messageBuilder;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductItemRepository productItemRepository;
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    public RespMessage getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return messageBuilder.buildSuccessMessage(orders);
    }

    public RespMessage getOrderById(long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setOrderDate(order.getOrderDate());
            orderResponse.setOrderItems(orderItems);
            orderResponse.setOrderStatus(order.getStatus().toString());
            orderResponse.setShippingAddress(order.getShippingAddress());
            orderResponse.setPaymentMethod(order.getPaymentMethod().toString());
            return messageBuilder.buildSuccessMessage(orderResponse);
        }
        throw new RuntimeException("Order not found");
    }

    @Transactional
    public RespMessage addOrder(OrderRequest orderRequest){
        if (orderRequest.getOrderItems().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[] {"order_items"}, "OrderItems cannot be empty");
        }
        Optional<ShippingAddress> shippingAddress = shippingAddressRepository.findById(orderRequest.getShippingAddressId());
        if ( shippingAddress.isEmpty() ){
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[] {"shipping_address"}, "ShippingAddress cannot be null");
        }
        Status shippingAddressStatus = shippingAddress.get().getStatus();
        if ( shippingAddressStatus.equals(Status.INACTIVE)){
            throw new CoffeeShopException(Constant.NOT_FOUND, new Object[] {"shipping_address"}, "ShippingAddress not found");
        }
        Order order = new Order();
        order.setShippingAddress(shippingAddress.get());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setStatus(OrderStatus.Processing);
        order.setOrderDate(new Date());
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest orderItemRequest : orderRequest.getOrderItems()) {
            long productItemId = orderItemRequest.getProductItemId();
            Optional<ProductItem> productItemOptional = productItemRepository.findById(productItemId);

            if (productItemOptional.isEmpty()) {
                throw new CoffeeShopException(Constant.NOT_FOUND,  new Object[] {"product_item"}, "ProductItem not found");
            }

            int stock = productItemOptional.get().getStock();
            int orderedAmount = orderItemRequest.getAmount();

            if (orderedAmount > stock) {
                throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[] {"order_amount"}, "Amount Item cannot be greater than stock");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductItem(productItemOptional.get());
            orderItem.setPrice(orderItemRequest.getPrice());
            orderItem.setDiscount(orderItemRequest.getDiscount());
            orderItem.setAmount(orderItemRequest.getAmount());
            orderItems.add(orderItem);
        }
        try {
            Order order1 = orderRepository.save(order);
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrder(order1);
                orderItemRepository.save(orderItem);
            }
            return messageBuilder.buildSuccessMessage(order1);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"order"}, "Order can not be added");
        }
    }

    public RespMessage updateOrderStatus(long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().equals(OrderStatus.Processing)){
                order.setStatus(OrderStatus.Awaiting);
            }
            if (order.getStatus().equals(OrderStatus.Awaiting)) {
                order.setStatus(OrderStatus.Shipping);
            }
            if (order.getStatus().equals(OrderStatus.Shipping)) {
                order.setStatus(OrderStatus.Completed);
            }
            try {
                orderRepository.save(order);
                return messageBuilder.buildSuccessMessage(order);
            } catch (CoffeeShopException e){
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{order}, "Order can not be updated");
            }
        }
        throw new RuntimeException("Order not found");
    }

    public RespMessage cancelOrder(long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().equals(OrderStatus.Processing)) {
                order.setStatus(OrderStatus.Cancelled);
                try {
                    orderRepository.save(order);
                    return messageBuilder.buildSuccessMessage(order);
                } catch ( Exception e ){
                    throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{order}, "Order can not be cancelled");
                }
            }
        }
        throw new RuntimeException("Order not found");
    }
}
