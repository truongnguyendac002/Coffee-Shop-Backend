package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.CartItem;
import com.ptit.coffee_shop.model.ProductItem;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.CartItemRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.CartItemRepository;
import com.ptit.coffee_shop.repository.ProductItemRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
