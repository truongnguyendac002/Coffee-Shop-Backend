package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.OrderStatus;
import com.ptit.coffee_shop.common.enums.PaymentMethod;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.*;
import com.ptit.coffee_shop.payload.request.OrderItemRequest;
import com.ptit.coffee_shop.payload.request.OrderRequest;
import com.ptit.coffee_shop.payload.response.OrderItemResponse;
import com.ptit.coffee_shop.payload.response.OrderResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.payload.response.ShippingAddressResponse;
import com.ptit.coffee_shop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    public RespMessage getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setOrderDate(order.getOrderDate());
            orderResponse.setOrderStatus(order.getStatus().toString());
            orderResponse.setPaymentMethod(order.getPaymentMethod().toString());
            orderResponse.setShippingAddress(order.getShippingAddress().toResponse());
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            List<OrderItemResponse> orderItemResponses = orderItems.stream().map(this::toOrderItemResponse).toList();
            double totalPrice = 0;
            for (OrderItem orderItem : orderItems) {
                totalPrice += (orderItem.getPrice() - orderItem.getDiscount())*orderItem.getAmount();
            }
            totalPrice += 10000;
            orderResponse.setOrderItems(orderItemResponses);
            orderResponse.setTotal(totalPrice);
            orderResponses.add(orderResponse);
        }
        return messageBuilder.buildSuccessMessage(orderResponses);
    }

    public RespMessage getOrderById(long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setOrderDate(order.getOrderDate());
            List<OrderItemResponse> orderItemResponses = orderItems.stream().map(this::toOrderItemResponse).toList();
            double totalPrice = 0;
            for (OrderItem orderItem : orderItems) {
                totalPrice += (orderItem.getPrice() - orderItem.getDiscount())*orderItem.getAmount();
            }
            totalPrice += 10000;
            orderResponse.setOrderItems(orderItemResponses);
            orderResponse.setTotal(totalPrice);
            orderResponse.setOrderStatus(order.getStatus().toString());
            orderResponse.setShippingAddress(order.getShippingAddress().toResponse());
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
            try {
                ProductItem productItem = productItemOptional.get();
                productItem.setStock(productItem.getStock() - orderedAmount);
                productItemRepository.save(productItem);
            } catch (Exception e) {
                throw new CoffeeShopException(Constant.UNDEFINED, new Object[] {"product_item"}, "Product item cannot be update at stock");
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
            return messageBuilder.buildSuccessMessage(order1.getId());
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"order"}, "Order can not be added");
        }
    }

    public RespMessage updateOrderStatus(long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().equals(OrderStatus.Processing)){
                order.setStatus(OrderStatus.Processed);
            } else if (order.getStatus().equals(OrderStatus.Processed)) {
                order.setStatus(OrderStatus.Shipping);
            } else if (order.getStatus().equals(OrderStatus.Shipping)) {
                order.setStatus(OrderStatus.Completed);
            } else {
                throw new RuntimeException("Order can not be updated");
            }
            try {
                orderRepository.save(order);
                return messageBuilder.buildSuccessMessage(order.getStatus());
            } catch (CoffeeShopException e){
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{order}, "Order can not be updated");
            }
        }
        throw new RuntimeException("Order not found");
    }

    @Transactional
    public RespMessage cancelOrder(long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().equals(OrderStatus.Processing)) {
                order.setStatus(OrderStatus.Cancelled);
            } else {
                throw new CoffeeShopException(Constant.UNDEFINED, new Object[]{order}, "Order can not be cancelled");
            }
            String paymentMethod = order.getPaymentMethod().toString();
            if (paymentMethod.equals(PaymentMethod.VNPay.toString())) {
                Transaction transaction1 = new Transaction();
                Optional<Transaction> transactionOptional = transactionRepository.findByOrderId(orderId);
                if (transactionOptional.isPresent()) {
                    Transaction transaction = transactionOptional.get();
                    transaction1.setTransactionNo(transaction.getTransactionNo());
                    transaction1.setAmount(transaction.getAmount());
                    transaction1.setOrder(transaction.getOrder());
                    transaction1.setCommand("refund");
                    transaction1.setTxnRef(transaction.getTxnRef());
                    transaction1.setPayDate(new Date());
                } else {
                    throw new CoffeeShopException(Constant.NOT_FOUND, null, "Transaction not found");
                }
                try {
                    orderRepository.save(order);
                    transactionRepository.save(transaction1);
                    return messageBuilder.buildSuccessMessage(order.getStatus());
                } catch (CoffeeShopException e){
                    throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{order}, "Order can not be cancelled");
                }
            } else {
                try {
                    orderRepository.save(order);
                    return messageBuilder.buildSuccessMessage(order.getStatus());
                } catch (CoffeeShopException e){
                    throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{order}, "Order can not be cancelled");
                }
            }
        }
        throw new RuntimeException("Order not found");
    }

    public RespMessage getOrdersByUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty())
            throw new CoffeeShopException(Constant.UNAUTHORIZED, null, "User not found by email: " + userEmail + "get from token!");
        User user = userOptional.get();
        List<Order> orders = orderRepository.findByUserId(user.getId());
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            List<OrderItemResponse> orderItemResponses = orderItems.stream().map(this::toOrderItemResponse).toList();
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setOrderDate(order.getOrderDate());
            orderResponse.setOrderItems(orderItemResponses);
            orderResponse.setOrderStatus(order.getStatus().toString());
            orderResponse.setShippingAddress(order.getShippingAddress().toResponse());
            orderResponse.setPaymentMethod(order.getPaymentMethod().toString());
            orderResponses.add(orderResponse);
        }

        return messageBuilder.buildSuccessMessage(orderResponses);
    }

    public RespMessage getOrderByStatus(OrderStatus status) {
        try {
            List<Order> orders = orderRepository.findByStatus(status);
            List<OrderResponse> orderResponses = new ArrayList<>();
            for (Order order : orders) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderId(order.getId());
                orderResponse.setOrderDate(order.getOrderDate());
                orderResponse.setOrderStatus(order.getStatus().toString());
                orderResponse.setPaymentMethod(order.getPaymentMethod().toString());
                orderResponse.setShippingAddress(order.getShippingAddress().toResponse());
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                List<OrderItemResponse> orderItemResponses = orderItems.stream().map(this::toOrderItemResponse).toList();
                double totalPrice = 0;
                for (OrderItem orderItem : orderItems) {
                    totalPrice += (orderItem.getPrice() - orderItem.getDiscount())*orderItem.getAmount();
                }
                totalPrice += 10000;
                orderResponse.setOrderItems(orderItemResponses);
                orderResponse.setTotal(totalPrice);
                orderResponses.add(orderResponse);
            }
            return messageBuilder.buildSuccessMessage(orderResponses);
        } catch (CoffeeShopException e) {
            throw new CoffeeShopException(Constant.UNDEFINED, null, "Order not found");
        }
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        List<Image> productImages = imageRepository.findByProduct(orderItem.getProductItem().getProduct());
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productItemId(orderItem.getProductItem().getId())
                .productId(orderItem.getProductItem().getProduct().getId())
                .productName(orderItem.getProductItem().getProduct().getName())
                .productType(orderItem.getProductItem().getType().getName())
                .amount(orderItem.getAmount())
                .price(orderItem.getPrice())
                .discount(orderItem.getDiscount())
                .isReviewed(orderItem.isReviewed())
                .productImage(productImages.get(0).getUrl())
                .build();
    }
}
