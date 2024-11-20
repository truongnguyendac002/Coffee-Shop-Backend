package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Brand;
import com.ptit.coffee_shop.model.Category;
import com.ptit.coffee_shop.model.Product;
import com.ptit.coffee_shop.model.TypeProduct;
import com.ptit.coffee_shop.payload.request.ProductRequest;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.BrandRepository;
import com.ptit.coffee_shop.repository.CategoryRepository;
import com.ptit.coffee_shop.repository.ProductRepository;
import com.ptit.coffee_shop.repository.TypeProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TypeProductRepository typeProductRepository;
    private final MessageBuilder messageBuilder;

    public RespMessage getAllProduct() {
        List<Product> products = productRepository.findAll();
        return messageBuilder.buildSuccessMessage(products);
    }

    public RespMessage getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return messageBuilder.buildSuccessMessage(product.get());
        } else {
            throw new CoffeeShopException( Constant.FIELD_NOT_FOUND , new Object[] {"product"} , "Product not found");
        }
    }


    public RespMessage addProduct(ProductRequest productRequest) {
        if (productRequest.getName() == null || productRequest.getName().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Product name must be not null");
        }
        if (productRequest.getCategoryId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"categoryId"}, "Category id invalid");
        }
        if (productRequest.getBrandId() <= 0) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"brandId"}, "Brand id invalid");
        }

        Optional<Category> categoryOptional = categoryRepository.findById(productRequest.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"categoryId"}, "Category id not found");
        }
        Optional<Brand> brandOptional = brandRepository.findById(productRequest.getBrandId());
        if (brandOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"brandId"}, "Brand id not found");
        }
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(categoryOptional.get());
        product.setBrand(brandOptional.get());
        try {
            productRepository.save(product);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when add product");
        }
        return messageBuilder.buildSuccessMessage(product);
    }

    @Transactional
    public RespMessage addCategory(String name , String description) {
        if (name == null || name.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Category name must be not null");
        }
        if (categoryRepository.findByName(name).isPresent()) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Category name is duplicate");
        }

        Category category = new Category();
        category.setName(name);
        if (description != null && !description.isEmpty()) {
            category.setDescription(description);
        }
        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when add category");
        }

        return messageBuilder.buildSuccessMessage(category);
    }

    @Transactional
    public RespMessage getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return  messageBuilder.buildSuccessMessage(categories);
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
