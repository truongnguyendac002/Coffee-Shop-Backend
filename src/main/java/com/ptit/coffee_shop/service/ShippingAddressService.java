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
            shippingAddressRequest.setRecieverName(shippingAddress.getRecieverName());
            shippingAddressRequest.setRecieverPhone(shippingAddress.getRecieverPhone());
            shippingAddressRequest.setLocation(shippingAddress.getLocation());
            shippingAddressRequests.add(shippingAddressRequest);
        }
        return messageBuilder.buildSuccessMessage(shippingAddressRequests);
    }

    public RespMessage addShippingAddress(long userId, ShippingAddressRequest shippingAddressRequest) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setUser(user1);
            shippingAddress.setRecieverName(shippingAddressRequest.getRecieverName());
            shippingAddress.setRecieverPhone(shippingAddressRequest.getRecieverPhone());
            shippingAddress.setLocation(shippingAddressRequest.getLocation());
            shippingAddress.setStatus(Status.ACTIVE);
            try {
                shippingAddressRepository.save(shippingAddress);
                return messageBuilder.buildSuccessMessage(shippingAddress);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be saved");
            }
        }
        throw new RuntimeException("User not found");
    }

    @Transactional
    public RespMessage updateShippingAddress(long userId, long shippingAddressId, ShippingAddressRequest shippingAddressRequest) {
        Optional<Order> order = orderRepository.findByShippingAddressId(shippingAddressId);
        if (order.isPresent()) {
            ShippingAddress shippingAddress = shippingAddressRepository.findById(shippingAddressId).orElse(null);
            shippingAddress.setStatus(Status.INACTIVE);
            ShippingAddress newShippingAddress = new ShippingAddress();
            User user = userRepository.findById(userId).orElse(null);
            newShippingAddress.setUser(user);
            newShippingAddress.setRecieverName(shippingAddressRequest.getRecieverName());
            newShippingAddress.setRecieverPhone(shippingAddressRequest.getRecieverPhone());
            newShippingAddress.setLocation(shippingAddressRequest.getLocation());
            newShippingAddress.setStatus(Status.ACTIVE);
            try {
                shippingAddressRepository.save(newShippingAddress);
                return messageBuilder.buildSuccessMessage(newShippingAddress);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be saved");
            }
        } else {
            ShippingAddress shippingAddress = shippingAddressRepository.findById(shippingAddressId).orElse(null);
            shippingAddress.setRecieverName(shippingAddressRequest.getRecieverName());
            shippingAddress.setRecieverPhone(shippingAddressRequest.getRecieverPhone());
            shippingAddress.setLocation(shippingAddressRequest.getLocation());
            try {
                shippingAddressRepository.save(shippingAddress);
                return messageBuilder.buildSuccessMessage(shippingAddress);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[] {"shipping_address"}, "Shipping address could not be saved");
            }
        }
    }
}
