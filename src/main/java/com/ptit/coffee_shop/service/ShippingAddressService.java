package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.CoffeeShopApplication;
import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Order;
import com.ptit.coffee_shop.model.ShippingAddress;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.ShippingAddressRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.OrderRepository;
import com.ptit.coffee_shop.repository.ShippingAddressRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShippingAddressService {
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageBuilder messageBuilder;
    @Autowired
    private OrderRepository orderRepository;

    public RespMessage getUserShippingAddresses(long userId) {
        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findByUserId(userId, Status.ACTIVE);
        List<ShippingAddressRequest> shippingAddressRequests = new ArrayList<>();
        for (ShippingAddress shippingAddress : shippingAddresses) {
            ShippingAddressRequest shippingAddressRequest = new ShippingAddressRequest();
            shippingAddressRequest.setId(shippingAddress.getId());
            shippingAddressRequest.setReceiverName(shippingAddress.getReceiverName());
            shippingAddressRequest.setReceiverPhone(shippingAddress.getReceiverPhone());
            shippingAddressRequest.setLocation(shippingAddress.getLocation());
            shippingAddressRequests.add(shippingAddressRequest);
        }
        return messageBuilder.buildSuccessMessage(shippingAddressRequests);
    }

    public RespMessage addShippingAddress(ShippingAddressRequest shippingAddressRequest) {
        Optional<User> user = userRepository.findById(shippingAddressRequest.getUserId());
        if (user.isPresent()) {
            User user1 = user.get();
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setUser(user1);
            shippingAddress.setReceiverName(shippingAddressRequest.getReceiverName());
            shippingAddress.setReceiverPhone(shippingAddressRequest.getReceiverPhone());
            shippingAddress.setLocation(shippingAddressRequest.getLocation());
            shippingAddress.setStatus(Status.ACTIVE);
            try {
                shippingAddressRepository.save(shippingAddress);
                return messageBuilder.buildSuccessMessage(shippingAddressRequest);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be saved");
            }
        }
        throw new RuntimeException("User not found");
    }

    @Transactional
    public RespMessage updateShippingAddress(ShippingAddressRequest shippingAddressRequest) {
        Optional<Order> order = orderRepository.findByShippingAddressId(shippingAddressRequest.getId());
        if (order.isPresent()) {
            ShippingAddress shippingAddress = shippingAddressRepository.findById(shippingAddressRequest.getId()).orElse(null);
            shippingAddress.setStatus(Status.INACTIVE);
            ShippingAddress newShippingAddress = new ShippingAddress();
            User user = userRepository.findById(shippingAddressRequest.getUserId()).orElse(null);
            newShippingAddress.setUser(user);
            newShippingAddress.setReceiverName(shippingAddressRequest.getReceiverName());
            newShippingAddress.setReceiverPhone(shippingAddressRequest.getReceiverPhone());
            newShippingAddress.setLocation(shippingAddressRequest.getLocation());
            newShippingAddress.setStatus(Status.ACTIVE);
            try {
                shippingAddressRepository.save(newShippingAddress);
                return messageBuilder.buildSuccessMessage(shippingAddressRequest);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be saved");
            }
        } else {
            ShippingAddress shippingAddress = shippingAddressRepository.findById(shippingAddressRequest.getId()).orElse(null);
            shippingAddress.setReceiverName(shippingAddressRequest.getReceiverName());
            shippingAddress.setReceiverPhone(shippingAddressRequest.getReceiverPhone());
            shippingAddress.setLocation(shippingAddressRequest.getLocation());
            try {
                shippingAddressRepository.save(shippingAddress);
                return messageBuilder.buildSuccessMessage(shippingAddressRequest);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be saved");
            }
        }
    }

    public RespMessage deleteShippingAddress(long shippingAddressId) {
        Optional<ShippingAddress> shippingAddress = shippingAddressRepository.findById(shippingAddressId);
        if (shippingAddress.isPresent()) {
            ShippingAddress shippingAddress1 = shippingAddress.get();
            shippingAddress1.setStatus(Status.INACTIVE);
            ShippingAddressRequest shippingAddressRequest = new ShippingAddressRequest();
            shippingAddressRequest.setId(shippingAddressId);
            shippingAddressRequest.setReceiverName(shippingAddressRequest.getReceiverName());
            shippingAddressRequest.setReceiverPhone(shippingAddressRequest.getReceiverPhone());
            shippingAddressRequest.setLocation(shippingAddressRequest.getLocation());
            shippingAddressRequest.setStatus(Status.INACTIVE);
            shippingAddressRequest.setUserId(shippingAddress1.getUser().getId());
            try {
                shippingAddressRepository.save(shippingAddress1);
                return messageBuilder.buildSuccessMessage(shippingAddressRequest);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be deleted");
            }
        }
        throw new RuntimeException("Shipping address not found");
    }
}
