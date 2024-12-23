package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.ShippingAddress;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.ShippingAddressRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.payload.response.ShippingAddressResponse;
import com.ptit.coffee_shop.repository.ShippingAddressRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

@RequiredArgsConstructor
public class ShippingAddressService {
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    private final MessageBuilder messageBuilder;
    public RespMessage getShippingAddress() {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CoffeeShopException(Constant.NOT_FOUND, null, "User not found with email: " + userEmail));
        List<ShippingAddress> shippingAddressList = shippingAddressRepository.findByUser(user)
                .orElseThrow(() -> new CoffeeShopException(Constant.NOT_FOUND, null, "Shipping address list not found with user"));
        if (shippingAddressList.isEmpty()) {
            return messageBuilder.buildSuccessMessage("");
        }

        List<ShippingAddressResponse> activeShippingAddressListResponse
                = shippingAddressList.stream()
                .filter(shippingAddress -> shippingAddress.getStatus().equals(Status.ACTIVE))
                .map(ShippingAddress::toResponse)
                .toList();
        return messageBuilder.buildSuccessMessage(activeShippingAddressListResponse);
    }

    public RespMessage addShippingAddress(ShippingAddressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CoffeeShopException(Constant.NOT_FOUND, null, "User not found with ID: " + request.getUserId()));
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setReceiverName(request.getReceiverName());
        shippingAddress.setReceiverPhone(request.getReceiverPhone());
        shippingAddress.setLocation(request.getLocation());
        shippingAddress.setUser(user);
        shippingAddressRepository.save(shippingAddress);
        return messageBuilder.buildSuccessMessage(shippingAddress.toResponse());
    }

    public RespMessage updateShippingAddress(ShippingAddressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CoffeeShopException(Constant.NOT_FOUND, null, "User not found with ID: " + request.getUserId()));
        Optional<ShippingAddress> shippingAddressOptional = shippingAddressRepository.findById(request.getId());
        if (shippingAddressOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.NOT_FOUND, null, "Shipping address not found with ID: " + request.getId());
        }
        ShippingAddress shippingAddress = shippingAddressOptional.get();
        shippingAddress.setReceiverName(request.getReceiverName());
        shippingAddress.setReceiverPhone(request.getReceiverPhone());
        shippingAddress.setLocation(request.getLocation());
        shippingAddress.setStatus(request.getStatus());
        shippingAddress.setUser(user);
        shippingAddressRepository.save(shippingAddress);
        return messageBuilder.buildSuccessMessage(shippingAddress.toResponse());
    }

    public RespMessage deleteShippingAddress(Long id) {
        Optional<ShippingAddress> shippingAddressOptional = shippingAddressRepository.findById(id);
        if (shippingAddressOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.NOT_FOUND, null, "Shipping address not found with ID: " + id);
        }
        ShippingAddress shippingAddress = shippingAddressOptional.get();
        shippingAddress.setStatus(Status.INACTIVE);
        shippingAddressRepository.save(shippingAddress);
//        return messageBuilder.buildSuccessMessage("Delete shipping address (id: " + id + ") successfully");
        return messageBuilder.buildSuccessMessage(shippingAddress.toResponse());
    }
}
