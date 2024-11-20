package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.request.FavoriteProductRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.FavoriteProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/favorites")
public class FavoriteProductController {
    private final FavoriteProductService favoriteProductService;
    public final MessageBuilder messageBuilder;


    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<RespMessage> getFavoriteProducts(@PathVariable Long userId) {
        try {
            RespMessage response = favoriteProductService.getFavoriteProducts(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An error occurred while fetching favorite products", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<RespMessage> addFavoriteProduct(@RequestBody FavoriteProductRequest request) {
        try {
            RespMessage response = favoriteProductService.addFavoriteProduct(request);
            return ResponseEntity.ok(response);
        } catch (CoffeeShopException e) {
            RespMessage resp = new RespMessage(e.getCode(), e.getMessage(), null);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An unexpected error occurred", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<RespMessage> removeFavoriteProduct(@RequestBody FavoriteProductRequest request) {
        try {
            RespMessage response = favoriteProductService.removeFavoriteProduct(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An error occurred while removing the product from favorites", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}