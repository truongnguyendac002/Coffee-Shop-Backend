package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.TypeProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeProductRepository extends JpaRepository<TypeProduct, Long> {
    Optional<TypeProduct> findByName(String name);
}
