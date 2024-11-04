package com.ptit.coffee_shop.controller;


import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.UserRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageBuilder messageBuilder;
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAllUsers() {
        RespMessage respMessage = userService.getAllUsers();
        return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
    }

    @PutMapping("/ban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespMessage> banUser(@PathVariable Long userId) {
        try {
            User bannedUser = userService.banUser(userId);
            return ResponseEntity.ok(messageBuilder.buildSuccessMessage(bannedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage())
                    );
        }
    }

    @PutMapping("/unban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespMessage> unbanUser(@PathVariable Long userId) {
        try {
            User unbannedUser = userService.unbanUser(userId);
            return ResponseEntity.ok(messageBuilder.buildSuccessMessage(unbannedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage()));
        }
    }

    @PutMapping("/update-profile/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RespMessage> updateUserProfile(@PathVariable Long userId, @RequestBody UserRequest updatedUser) {
        try {
            User savedUser = userService.updateUserInfo(userId, updatedUser);
            return ResponseEntity.ok(messageBuilder.buildSuccessMessage(savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RespMessage> getUserInfo(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(messageBuilder.buildSuccessMessage(user));
        } catch (CoffeeShopException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage()));
        } catch ( RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage()));
        }
    }
}
