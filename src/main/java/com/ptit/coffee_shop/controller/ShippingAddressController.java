package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.ShippingAddress;
import com.ptit.coffee_shop.payload.request.ShippingAddressRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")

public class ShippingAddressController {
    @Autowired
    private ShippingAddressService shippingAddressService;
    @Autowired
    private MessageBuilder messageBuilder;

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUserShippingAddress(@PathVariable long userId) {
        RespMessage respMessage = shippingAddressService.getUserShippingAddresses(userId);
        return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
    }

    @PostMapping("/{userId}/add-shipping-address")
    public ResponseEntity<String> addShippingAddress(@PathVariable long userId, @RequestBody ShippingAddressRequest shippingAddressRequest) {
        try {
            RespMessage respMessage = shippingAddressService.addShippingAddress(userId, shippingAddressRequest);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/update-shipping-address/{shippingAddressId}")
    public ResponseEntity<String> updateShippingAddress(@PathVariable long userId, @PathVariable long shippingAddressId, @RequestBody ShippingAddressRequest shippingAddressRequest) {
        try {
            RespMessage respMessage = shippingAddressService.updateShippingAddress(userId, shippingAddressId, shippingAddressRequest);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
    }
}
