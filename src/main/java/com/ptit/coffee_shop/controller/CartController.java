package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.payload.request.CartItemRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/cart")
public class CartController {
    @RequestMapping(value = "user", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getCartItems(@RequestParam String userId) {
        return ResponseEntity.ok("Hello");
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> addCartItem(@RequestBody CartItemRequest request) {
return ResponseEntity.ok("Hello");
    }
}
