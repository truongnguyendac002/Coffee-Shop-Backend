package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Brand;
import com.ptit.coffee_shop.model.Category;
import com.ptit.coffee_shop.model.TypeProduct;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.BrandRepository;
import com.ptit.coffee_shop.repository.CategoryRepository;
import com.ptit.coffee_shop.repository.ProductRepository;
import com.ptit.coffee_shop.repository.TypeProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TypeProductRepository typeProductRepository;
    private final MessageBuilder messageBuilder;

//    public Product addProduct(ProductRequest productRequest) {
//        Product product = new Product();
//        product.setName(productRequest.getName());
//        product.setPrice(productRequest.getPrice());
//        product.setCategory(categoryRepository.findByName(productRequest.getCategory()).orElseThrow(() -> new RuntimeException("Category not found")));
//        product.setBrand(brandRepository.findByName(productRequest.getBrand()).orElseThrow(() -> new RuntimeException("Brand not found")));
//        product.setTypeProduct(typeProductRepository.findByName(productRequest.getTypeProduct()).orElseThrow(() -> new RuntimeException("Type product not found")));
//        return productRepository.save(product);
//    }

    @Transactional
    public RespMessage addCategory(String name) {
        if (name == null || name.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Category name must be not null");
        }
        if (categoryRepository.findByName(name).isPresent()) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Category name is duplicate");
        }
        Category category = new Category();
        category.setName(name);
        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when add category");
        }

        return messageBuilder.buildSuccessMessage(category);
    }

    @Transactional
    public RespMessage addBrand(String name) {
        if (name == null || name.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Brand name must be not null");
        }
        if (brandRepository.findByName(name).isPresent()) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Brand name is duplicate");
        }
        Brand brand = new Brand();
        brand.setName(name);
        try {
            brandRepository.save(brand);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when add brand");
        }
        return messageBuilder.buildSuccessMessage(brand);
    }

    @Transactional
    public RespMessage addTypeProduct(String name) {
        if (name == null || name.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Type product name must be not null");
        }
        if (typeProductRepository.findByName(name).isPresent()) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Type product name is duplicate");
        }
        TypeProduct typeProduct = new TypeProduct();
        typeProduct.setName(name);
        try {
            typeProductRepository.save(typeProduct);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when add type product");
        }
        return messageBuilder.buildSuccessMessage(typeProduct);
    }

}
