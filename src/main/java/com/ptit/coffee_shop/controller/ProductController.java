package com.ptit.coffee_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/product")
public class ProductController {
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String > getAllProduct() {
        int a = 1;
        int b = 2;
        int c = a + b;
        return ResponseEntity.ok("Hello");
    }
}
