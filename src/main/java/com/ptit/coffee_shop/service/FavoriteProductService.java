package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.FavoriteProduct;
import com.ptit.coffee_shop.model.Product;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.FavoriteProductRequest;
import com.ptit.coffee_shop.payload.response.FavoriteProductResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.FavoriteProductRepository;
import com.ptit.coffee_shop.repository.ProductRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final ProductRepository productRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final MessageBuilder messageBuilder;
    public final UserRepository userRepository;

    // Get list of favorite products for a specific user
    public RespMessage getFavoriteProducts(Long userId) {
        if (userId <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"UserId"}, "UserId invalid");
        }

        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findByUserId(userId);
        List<FavoriteProductResponse> favoriteProductResponses = favoriteProducts.stream()
                .map(favoriteProduct -> new FavoriteProductResponse(
                        favoriteProduct.getId(),
                        favoriteProduct.getProduct(),
                        favoriteProduct.getUser().getId()
                )).collect(Collectors.toList());

        return messageBuilder.buildSuccessMessage(favoriteProductResponses);
    }

    // Add a product to the user's favorite list
    public RespMessage addFavoriteProduct(FavoriteProductRequest request) {
        if (request.getUserId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"UserId"}, "UserId invalid");
        }
        if (request.getProductId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"ProductId"}, "ProductId invalid");
        }

        Optional<Product> product = productRepository.findById(request.getProductId());
        if (product.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"Product"}, "Product not found");
        }

        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"UserId"}, "UserId not found");
        }

        if (favoriteProductRepository.existsByUserIdAndProductId(request.getUserId(), request.getProductId())) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"FavoriteProduct"}, "This product is already in favorites");
        }

        FavoriteProduct favoriteProduct = FavoriteProduct.builder()
                .product(product.get())
                .user(user.get())
                .build();
        favoriteProduct = favoriteProductRepository.save(favoriteProduct);

        FavoriteProductResponse response = new FavoriteProductResponse(
                favoriteProduct.getId(),
                favoriteProduct.getProduct(),
                favoriteProduct.getUser().getId()
        );

        return messageBuilder.buildSuccessMessage(response);
    }


    // Remove a product from the user's favorite list
    public RespMessage removeFavoriteProduct(FavoriteProductRequest request) {
        Optional<FavoriteProduct> favoriteProductOpt = favoriteProductRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId());

        if (favoriteProductOpt.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"FavoriteProduct"}, "Product not found in favorites");
        }

        favoriteProductRepository.delete(favoriteProductOpt.get());
        return messageBuilder.buildSuccessMessage("Product successfully removed from favorites");
    }
}
