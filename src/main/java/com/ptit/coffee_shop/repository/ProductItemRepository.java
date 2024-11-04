package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

    boolean existsByProductIdAndTypeId(long productId, long typeId);
}
