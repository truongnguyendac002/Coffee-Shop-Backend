package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Image;
import com.ptit.coffee_shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProduct(Product product);
    void deleteByProduct(Product product);
}
