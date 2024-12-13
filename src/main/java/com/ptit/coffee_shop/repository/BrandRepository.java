package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
    @Query("SELECT b FROM Brand b WHERE b.status = 'ACTIVE'")
    List<Brand> getAll();
}
