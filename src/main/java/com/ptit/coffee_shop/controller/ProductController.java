package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.request.ProductRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    private final MessageBuilder messageBuilder;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getAllProducts() {
        RespMessage respMessage = productService.getAllProduct();
        return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
    }
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addProduct(@RequestBody ProductRequest request) {
        try {
            RespMessage respMessage = productService.addProduct(request);
            return  new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        }
        catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateProduct() {
        return ResponseEntity.ok("Hello");
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = "application/json")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct() {
        return ResponseEntity.ok("Hello");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> getProduct() {
        return ResponseEntity.ok("Hello");
    }
}
