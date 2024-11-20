package com.ptit.coffee_shop.repository;


import com.ptit.coffee_shop.model.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    List<FavoriteProduct> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<FavoriteProduct> findByUserIdAndProductId(Long userId, Long productId);
}
