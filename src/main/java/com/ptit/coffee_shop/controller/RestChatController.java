package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class RestChatController {
    private final ChatService chatService;
    private final MessageBuilder messageBuilder;
    @RequestMapping(value = "/conversation/all", method = RequestMethod.GET, produces = "application/json")

    public ResponseEntity<String> getAllConversation() {
        try {
            RespMessage respMessage = chatService.getAllConversation();
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/conversation/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getConversationById
            (
                    @PathVariable("id") long id
            ) {
        try {
            RespMessage respMessage = chatService.getConversationById(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/conversation/user/{id}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getConversationByUser
            (
                    @PathVariable("id") long id
            ) {
        try {
            RespMessage respMessage = chatService.getConversationByHostId(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/conversation/user/{id}", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createConversation
            (
                    @PathVariable("id") long id
            ) {
        try {
            RespMessage respMessage = chatService.createConversation(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(),e.getObjects(),e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.NOT_FOUND, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.NOT_FOUND);
        }
    }


}
