package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    List<ProductItem> findByProductId(long productId);
    List<ProductItem> findByTypeId(long typeId);
    List<ProductItem> findByPrice(double price);
    List<ProductItem> findByStock(int stock);
    List<ProductItem> findByDiscount(double discount);
    List<ProductItem> findByProductIdAndTypeId(long productId, long typeId);
    List<ProductItem> findByProductIdAndPrice(long productId, double price);
    List<ProductItem> findByProductIdAndStock(long productId, int stock);
    List<ProductItem> findByProductIdAndDiscount(long productId, double discount);
    List<ProductItem> findByTypeIdAndPrice(long typeId, double price);
    List<ProductItem> findByTypeIdAndStock(long typeId, int stock);
    List<ProductItem> findByTypeIdAndDiscount(long typeId, double discount);
    List<ProductItem> findByPriceAndStock(double price, int stock);
    List<ProductItem> findByPriceAndDiscount(double price, double discount);
    List<ProductItem> findByStockAndDiscount(int stock, double discount);
    List<ProductItem> findByProductIdAndTypeIdAndPrice(long productId, long typeId, double price);
    List<ProductItem> findByProductIdAndTypeIdAndStock(long productId, long typeId, int stock);
    List<ProductItem> findByProductIdAndTypeIdAndDiscount(long productId, long typeId, double discount);
    List<ProductItem> findByProductIdAndPriceAndStock(long productId, double price, int stock);
    List<ProductItem> findByProductIdAndPriceAndDiscount(long productId, double price, double discount);
    List<ProductItem> findByProductIdAndStockAndDiscount(long productId, int stock, double discount);
    List<ProductItem> findByTypeIdAndPriceAndStock(long typeId, double price, int stock);
    List<ProductItem> findByTypeIdAndPriceAndDiscount(long typeId, double price, double discount);
    List<ProductItem> findByTypeIdAndStockAndDiscount(long typeId, int stock, double discount);
    List<ProductItem> findByPriceAndStockAndDiscount(double price, int stock, double discount);
    List<ProductItem> findByProductIdAndTypeIdAndPriceAndStock(long productId, long typeId, double price, int stock);
    List<ProductItem> findByProductIdAndTypeIdAndPriceAndDiscount(long productId, long typeId, double price, double discount);
    List<ProductItem> findByProductIdAndTypeIdAndStockAndDiscount(long productId, long typeId, int stock, double discount);
    List<ProductItem> findByProductIdAndPriceAndStockAndDiscount(long productId, double price , int stock, double discount);
}
