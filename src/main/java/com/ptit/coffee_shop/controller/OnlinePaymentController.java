package com.ptit.coffee_shop.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.config.OnlinePaymentConfig;
import com.ptit.coffee_shop.payload.response.PaymentResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.OnlinePaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
public class OnlinePaymentController {

    private final MessageBuilder messageBuilder;
    private final OnlinePaymentService onlinePaymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createOnlinePayment(@RequestParam("amount") int amount, HttpServletRequest request ) {
        try {
            RespMessage respMessage = onlinePaymentService.createVNPayPayment(amount,request);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.SYSTEM_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handleVNPayReturn(HttpServletRequest request) {
        try {
            RespMessage respMessage = onlinePaymentService.handleVNPayReturn(request);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (RuntimeException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.SYSTEM_ERROR, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
