package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(String keyword);

    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT MAX(pi.price) FROM ProductItem pi WHERE pi.product.id = :productId AND pi.stock > 0 AND pi.status = 'ACTIVE'")
    Optional<Double> maxPrice(long productId);

    @Query("SELECT MIN(pi.price) FROM ProductItem pi WHERE pi.product.id = :productId AND pi.stock > 0 AND pi.status = 'ACTIVE'")
    Optional<Double> minPrice(long productId);
}
