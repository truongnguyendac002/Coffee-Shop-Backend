package com.ptit.coffee_shop.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Category;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.service.CategoryService;
import com.ptit.coffee_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    public final ProductService productService;
    public final MessageBuilder messageBuilder;
    private final CategoryService categoryService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json",
                        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addCategory(@RequestParam("name") String name, @RequestParam("description") String description,
                                              @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            RespMessage respMessage = categoryService.addCategory(name, description, imageFile);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        }
        catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/all" , method = RequestMethod.GET , produces = "application/json")
    public ResponseEntity<String> getAllCategory () {
        RespMessage respMessage = categoryService.getAllCategories();
        return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.GET , produces = "application/json")
    public ResponseEntity<String> getCategory (@PathVariable long id) {
        try {
            RespMessage respMessage = categoryService.getCategoryById(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.PUT , produces = "application/json",
                    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateCategory (@PathVariable Long id,
                                                  @RequestParam("name") String name, @RequestParam("description") String description,
                                                  @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            RespMessage respMessage = categoryService.updateCategory(id, name, description,imageFile);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.DELETE , produces = "application/json")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCategory (@PathVariable long id) {
        try {
            RespMessage respMessage = categoryService.deleteCategory(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        } catch (CoffeeShopException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.BAD_REQUEST);
        }
    }

}
