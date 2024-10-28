package com.ptit.coffee_shop.controller;


import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get-all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/ban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespMessage> banUser(@PathVariable Long userId) {
        try {
            User bannedUser = userService.banUser(userId);
            return ResponseEntity.ok(
                    RespMessage.builder()
                            .respCode("00")
                            .respDesc("User has been banned successfully")
                            .data(bannedUser)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            RespMessage.builder()
                                    .respCode("01")
                                    .respDesc(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }

    @PutMapping("/unban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespMessage> unbanUser(@PathVariable Long userId) {
        try {
            User unbannedUser = userService.unbanUser(userId);
            return ResponseEntity.ok(
                    RespMessage.builder()
                            .respCode("00")
                            .respDesc("User has been unbanned successfully")
                            .data(unbannedUser)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            RespMessage.builder()
                                    .respCode("01")
                                    .respDesc(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }

    @PutMapping("/update-profile/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RespMessage> updateUserProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
        try {
            User savedUser = userService.updateUserInfo(userId, updatedUser);
            return ResponseEntity.ok(
                    RespMessage.builder()
                            .respCode("00")
                            .respDesc("User info updated successfully")
                            .data(savedUser)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            RespMessage.builder()
                                    .respCode("01")
                                    .respDesc(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }
}
