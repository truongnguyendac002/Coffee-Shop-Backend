package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query("SELECT cg FROM Category cg WHERE cg.status = 'ACTIVE'")
    List<Category> findAllCategories();
}
