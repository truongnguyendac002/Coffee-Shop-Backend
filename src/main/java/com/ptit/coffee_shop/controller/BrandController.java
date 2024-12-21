package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Brand;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.BrandService;
import com.ptit.coffee_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brand")
public class BrandController {
    private final ProductService productService;
    private final MessageBuilder messageBuilder;
    private final BrandService brandService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addBrand(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        try {
            RespMessage respMessage = productService.addBrand(name);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getAllBrand() {
        RespMessage respMessage = brandService.getAllBrands();
        return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateBrand(@PathVariable long id, @RequestBody Brand brand) {
        try {
            RespMessage respMessage = brandService.updateBrand(id,brand);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBrand(@PathVariable long id) {
        try {
            RespMessage respMessage = brandService.deleteBrand(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }
    }
}
