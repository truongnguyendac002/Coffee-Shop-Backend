package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Product;
import com.ptit.coffee_shop.model.ProductItem;
import com.ptit.coffee_shop.model.TypeProduct;
import com.ptit.coffee_shop.payload.request.ProductItemRequest;
import com.ptit.coffee_shop.payload.response.ProductItemResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.ProductItemRepository;
import com.ptit.coffee_shop.repository.ProductRepository;
import com.ptit.coffee_shop.repository.TypeProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductItemService {
    public final ProductItemRepository productItemRepository;
    public final ProductRepository productRepository;
    public final TypeProductRepository typeProductRepository;
    public final MessageBuilder messageBuilder;
    private  final  CartService cartService;

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

        ProductItem productItem = new ProductItem();
        productItem.setPrice(request.getPrice());
        productItem.setStock(request.getStock());
        productItem.setDiscount(request.getDiscount());
        productItem.setProduct(productOptional.get());
        productItem.setType(typeProductOptional.get());
        try {
            productItemRepository.save(productItem);
        } catch (Exception e) {
            log.error("ProductItem can not be added", e);
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e}, "ProductItem can not be added");
        }
        return messageBuilder.buildSuccessMessage(productItem);
    }


    public RespMessage getProductItem(Long productId) {
        List<ProductItem> productItems = productItemRepository.findByProductId(productId);
        List<ProductItemResponse> activeProductItemResponses = productItems.stream()
                .filter(productItem -> productItem.getStatus() == Status.ACTIVE)
                .map(cartService::toProductItemResponse)
                .toList();
        return messageBuilder.buildSuccessMessage(activeProductItemResponses);
    }


    public RespMessage updateProductItem(ProductItemRequest request, long id) {
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
        Optional<ProductItem> productItemOptional = productItemRepository.findById(id);
        if (productItemOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"ProductItem"}, "ProductItem not found");
        }
        ProductItem productItem = productItemOptional.get();
        productItem.setPrice(request.getPrice());
        productItem.setStock(request.getStock());
        productItem.setDiscount(request.getDiscount());
        productItem.setProduct(productOptional.get());
        productItem.setType(typeProductOptional.get());
        try {
            productItemRepository.save(productItem);
        } catch (Exception e) {
            log.error("ProductItem can not be updated", e);
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e}, "ProductItem can not be updated");
        }
        return messageBuilder.buildSuccessMessage(productItem);
    }

    public RespMessage deleteProductItem(long id) {
        Optional<ProductItem> productItemOptional = productItemRepository.findById(id);
        if (productItemOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"ProductItem"}, "ProductItem not found");
        }
        try {
            ProductItem productItem = productItemOptional.get();
            productItem.setStatus(Status.INACTIVE);
            productItemRepository.save(productItem);
        } catch (Exception e) {
            log.error("ProductItem can not be deleted", e);
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e}, "ProductItem can not be deleted");
        }
        return messageBuilder.buildSuccessMessage(productItemOptional.get());
    }
}
