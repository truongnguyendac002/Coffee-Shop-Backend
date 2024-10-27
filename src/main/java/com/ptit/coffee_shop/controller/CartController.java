package com.ptit.coffee_shop.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getCart(@RequestBody String UserId) {
        return ResponseEntity.ok("Hello");
    }

}
