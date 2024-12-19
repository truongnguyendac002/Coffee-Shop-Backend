package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.*;
import com.ptit.coffee_shop.payload.request.CartItemRequest;
import com.ptit.coffee_shop.payload.response.CartItemResponse;
import com.ptit.coffee_shop.payload.response.ProductItemResponse;
import com.ptit.coffee_shop.payload.response.ProductResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.CartItemRepository;
import com.ptit.coffee_shop.repository.ImageRepository;
import com.ptit.coffee_shop.repository.ProductItemRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final MessageBuilder messageBuilder;
    public final ProductItemRepository productItemRepository;
    public final CartItemRepository cartItemRepository;
    public final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ProductService productService;

    @Transactional
    public RespMessage addCartItem(CartItemRequest request) {
        if (request.getQuantity() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Quantity"}, "Quantity must be greater than 0");
        }
        if (request.getUserId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"UserId"}, "UserId invalid");
        }
        if (request.getProductItemId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"ProductItemId"}, "ProductItemId invalid");
        }

        Optional<ProductItem> productItemOpt = productItemRepository.findById(request.getProductItemId());
        if (productItemOpt.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"ProductItem"}, "ProductItem not found");
        }
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"UserId"}, "UserId not found");
        }
        try {

            ProductItem productItem = productItemOpt.get();
            User user = userOpt.get();


            Optional<CartItem> cartItemOptional = cartItemRepository.findByUserIdAndProductItemId(request.getUserId(), request.getProductItemId());
            CartItem cartItem;
            if (cartItemOptional.isPresent()) {
                cartItem = cartItemOptional.get();
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            } else {

                cartItem = CartItem.builder()
                        .productItem(productItem)
                        .quantity(request.getQuantity())
                        .user(user)
                        .build();
            }
            cartItemRepository.save(cartItem);

            ProductItemResponse productItemResponse = toProductItemResponse(productItem);


            CartItemResponse cartItemResponse = new CartItemResponse(
                    cartItem.getId(),
                    productItemResponse,
                    cartItem.getQuantity(),
                    cartItem.getUser().getId()
            );

            return messageBuilder.buildSuccessMessage(cartItemResponse);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"CartItem"}, "Save Cart Item failed");
        }
    }

    public RespMessage getCartItems(Long userId) {
        if (userId <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"UserId"}, "UserId invalid");
        }
        try {

            List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
            List<CartItemResponse> cartItemResponses = cartItems.stream().map(cartItem -> {
                ProductItemResponse productItemResponse = toProductItemResponse(cartItem.getProductItem());
                return new CartItemResponse(
                        cartItem.getId(),
                        productItemResponse,
                        cartItem.getQuantity(),
                        cartItem.getUser().getId()
                );
            }).collect(Collectors.toList());
            return messageBuilder.buildSuccessMessage(cartItemResponses);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"CartItem"}, "Get Cart Item failed");
        }
    }

    public RespMessage updateCartItem( CartItemRequest cartItemRequest) {
        ProductItem productItem = productItemRepository.findById(cartItemRequest.getProductItemId())
                .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"ProductItem"}, "ProductItem not found"));

        User user = userRepository.findById(cartItemRequest.getUserId()).orElseThrow(
                () -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"UserId"}, "UserId not found"));

        CartItem cartItem = cartItemRepository.findByUserIdAndProductItemId(cartItemRequest.getUserId(), cartItemRequest.getProductItemId())
                .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"CartItem"}, "CartItem not found"));

        if (cartItemRequest.getQuantity() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Quantity"}, "Quantity must be greater than 0");
        }
        if (cartItemRequest.getQuantity() > productItem.getStock()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Quantity"}, "Quantity must be less than or equal to ProductItem quantity");
        }

        cartItem.setQuantity(cartItemRequest.getQuantity());

        try {
            cartItemRepository.save(cartItem);
//            return messageBuilder.buildSuccessMessage(cartItem);
            return messageBuilder.buildSuccessMessage("update cartItem thành công ");

        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"CartItem"}, "Update Cart Item failed");
        }
    }

    public RespMessage deleteCartItem(Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"CartItem"},
                        "Cart Item not found with Id: " + itemId));
        cartItemRepository.delete(cartItem);
        return messageBuilder.buildSuccessMessage("Delete cart Item successfully");
    }

    public ProductItemResponse toProductItemResponse(ProductItem productItem) {
        return ProductItemResponse.builder()
                .id(productItem.getId())
                .price(productItem.getPrice())
                .stock(productItem.getStock())
                .discount(productItem.getDiscount())
                .productResponse(productService.getProductResponse(productItem.getProduct()))
                .status(productItem.getStatus())
                .type(productItem.getType())
                .build();
    }

}
