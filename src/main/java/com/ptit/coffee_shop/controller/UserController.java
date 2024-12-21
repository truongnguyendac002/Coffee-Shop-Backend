package com.ptit.coffee_shop.controller;


import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.request.UserRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageBuilder messageBuilder;
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAllUsers() {
        try
        {
            RespMessage respMessage = userService.getAllUsers();
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        }
        catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.SYSTEM_ERROR, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> banUser(@PathVariable Long userId) {
        try {
            RespMessage respMessage = userService.banUser(userId);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unbanUser(@PathVariable Long userId) {
        try {
            RespMessage respMessage = userService.unbanUser(userId);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateUserProfile(@PathVariable Long userId, @RequestBody UserRequest updatedUser) {
        try {
            RespMessage respMessage = userService.updateUserInfo(updatedUser);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getUserInfo(@PathVariable Long userId) {
        try {
            RespMessage respMessage = userService.getUserById();
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch ( RuntimeException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }
}
