package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.request.ReviewRequet;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.ReviewRepository;
import com.ptit.coffee_shop.service.ReviewService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.MessageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final MessageBuilder messageBuilder;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> addReview(@RequestBody ReviewRequet reviewRequet) {
        try {
            RespMessage respMessage = reviewService.addReview(reviewRequet);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/{reviewId}", method = RequestMethod.DELETE, produces = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") long reviewId) {
        try {
            RespMessage respMessage = reviewService.deleteReview(reviewId);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e){
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/product/{productId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getReviewsByProductId(@PathVariable("productId") long productId) {
        try {
            RespMessage respMessage = reviewService.getReviewByProductId(productId);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e){
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
    }
}
