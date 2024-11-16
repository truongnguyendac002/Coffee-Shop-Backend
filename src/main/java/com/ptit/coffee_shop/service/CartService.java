package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.CartItem;
import com.ptit.coffee_shop.model.ProductItem;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.CartItemRequest;
import com.ptit.coffee_shop.payload.response.CartItemResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.CartItemRepository;
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

        Optional<ProductItem> productItem = productItemRepository.findById(request.getProductItemId());
        if (productItem.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"ProductItem"}, "ProductItem not found");
        }
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"UserId"}, "UserId not found");
        }
        try {
            // update quantity if cart item already exists
            Optional<CartItem> cartItemOptional = cartItemRepository.findByUserIdAndProductItemId(request.getUserId(), request.getProductItemId());
            CartItem cartItem;
            if (cartItemOptional.isPresent()) {
                cartItem = cartItemOptional.get();
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            } else {
                // create new cart item if not exists
                cartItem = CartItem.builder()
                        .productItem(productItem.get())
                        .quantity(request.getQuantity())
                        .user(user.get())
                        .build();
            }
            cartItemRepository.save(cartItem);
            return messageBuilder.buildSuccessMessage(cartItem);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"CartItem"}, "Save Cart Item failed");
        }
    }

//    public RespMessage getCartItems(Long userId) {
//        if (userId <= 0) {
//            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"UserId"}, "UserId invalid");
//        }
//        try {
//            return messageBuilder.buildSuccessMessage(cartItemRepository.findByUserId(userId));
//        } catch (Exception e) {
//            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"CartItem"}, "Get Cart Item failed");
//        }
//    }
    public RespMessage getCartItems(Long userId) {
        if (userId <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"UserId"}, "UserId invalid");
        }
        try {

            List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

            List<CartItemResponse> cartItemResponses = cartItems.stream().map(cartItem ->
                    new CartItemResponse(
                            cartItem.getId(),
                            cartItem.getProductItem(),
                            cartItem.getQuantity(),
                            cartItem.getUser().getId()
                    )
            ).collect(Collectors.toList());

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
            return messageBuilder.buildSuccessMessage(cartItem);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{"CartItem"}, "Update Cart Item failed");
        }
    }
}
