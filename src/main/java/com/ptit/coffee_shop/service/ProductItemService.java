package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Product;
import com.ptit.coffee_shop.model.ProductItem;
import com.ptit.coffee_shop.model.TypeProduct;
import com.ptit.coffee_shop.payload.request.ProductItemRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.ProductItemRepository;
import com.ptit.coffee_shop.repository.ProductRepository;
import com.ptit.coffee_shop.repository.TypeProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductItemService {
    public final ProductItemRepository productItemRepository;
    public final ProductRepository productRepository;
    public final TypeProductRepository typeProductRepository;
    public final MessageBuilder messageBuilder;
    public String getProductItems() {
        return "Hello";
    }

    public RespMessage addProductItem(ProductItemRequest request) {
        if (request.getPrice() < 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Price"}, "Price can not be negative");
        }
        if (request.getStock() < 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Stock"}, "Stock can not be negative");
        }
        if (request.getDiscount() < 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Discount"}, "Discount can not be negative");
        }
        if (request.getProductId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"ProductId"}, "Product id must be greater than 0");
        }
        if (request.getTypeId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"TypeId"}, "Type id must be greater than 0");
        }
        Optional<Product> productOptional = productRepository.findById(request.getProductId());
        if (productOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"ProductId"}, "Product id not found");
        }
        Optional<TypeProduct> typeProductOptional = typeProductRepository.findById(request.getTypeId());
        if (typeProductOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"TypeId"}, "Type id not found");
        }
        if (productItemRepository.existsByProductIdAndTypeId(request.getProductId(), request.getTypeId())) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"ProductItem"}, "ProductItem already exists");
        }

        ProductItem productItem = new ProductItem();
        productItem.setPrice(request.getPrice());
        productItem.setStock(request.getStock());
        productItem.setDiscount(request.getDiscount());
        productItem.setProduct(productOptional.get());
        productItem.setType(typeProductOptional.get());
        try {
            productItemRepository.save(productItem);
        }
        catch (Exception e) {
            log.error("ProductItem can not be added", e);
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e}, "ProductItem can not be added");
        }
        return messageBuilder.buildSuccessMessage(productItem);
    }

    public String updateProductItem() {
        return "Hello";
    }

    public String deleteProductItem() {
        return "Hello";
    }

    public String getProductItem() {
        return "Hello";
    }
}
