package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.*;
import com.ptit.coffee_shop.payload.request.ProductRequest;
import com.ptit.coffee_shop.payload.response.ProductResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.payload.response.ReviewResponse;
import com.ptit.coffee_shop.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TypeProductRepository typeProductRepository;
    private final ReviewRepository reviewRepository;
    private final MessageBuilder messageBuilder;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;
    private final OrderItemRepository orderItemRepository;
    public RespMessage getAllProduct() {
        List<Product> products = productRepository.findAll();
        List<Product> activeProducts = products.stream().filter(product -> product.getStatus() == Status.ACTIVE).toList();
        List<ProductResponse> productResponseList = new ArrayList<>();
        for (Product product : activeProducts) {
            List<Image> images = imageRepository.findByProduct(product);
            ProductResponse productResponse = getProductResponse(product);
            productResponse.setImages(images);
            productResponseList.add(productResponse);
        }
        return messageBuilder.buildSuccessMessage(productResponseList);
    }

    public RespMessage getProductById(Long id) {
        Optional<Product> productOp = productRepository.findById(id);

        if (productOp.isPresent()) {
            Product product = productOp.get();
            if (product.getStatus() == Status.INACTIVE) {
                throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"product"}, "Product not active");
            }
            ProductResponse productResponse = getProductResponse(product);
            List<Image> images = imageRepository.findByProduct(product);
            productResponse.setImages(images);
            return messageBuilder.buildSuccessMessage(productResponse);
        } else {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"product"}, "Product not found");
        }
    }
    public RespMessage getProductsByCategoryId(Long categoryId) {
        try {
            List<Product> tempProducts = productRepository.findByCategoryId(categoryId);
            List<Product> products = tempProducts.stream().filter(product -> product.getStatus() == Status.ACTIVE).toList();
            List<ProductResponse> productResponseList = new ArrayList<>();
            for (Product product : products) {
                ProductResponse productResponse = getProductResponse(product);
                productResponseList.add(productResponse);
            }
            return messageBuilder.buildSuccessMessage(productResponseList);
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, null , null);
        }
    }


    // Tìm kiếm sản phẩm theo từ khóa và trả về RespMessage
    public RespMessage searchProductsByKeyword(String keyword) {
        try {
            List<Product> products = productRepository
                    .searchByKeyword(keyword)
                    .stream().filter(product -> product.getStatus() == Status.ACTIVE).toList();
            if (products.isEmpty()) {
                return messageBuilder.buildFailureMessage(Constant.FIELD_NOT_FOUND, null, null);
            }
            List<ProductResponse> productResponseList = new ArrayList<>();
            for (Product product : products) {
                ProductResponse productResponse = getProductResponse(product);
                productResponseList.add(productResponse);
            }
            return messageBuilder.buildSuccessMessage(productResponseList);
        } catch (Exception e) {
            // Xây dựng phản hồi thất bại khi có lỗi
            return messageBuilder.buildFailureMessage(Constant.SYSTEM_ERROR, null, null);
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
        return messageBuilder.buildSuccessMessage(getProductResponse(product));
    }

    @Transactional
    public RespMessage addBrand(String name) {
        if (name == null || name.trim().isEmpty()) {
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

    public RespMessage deleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"product"}, "Product not found");
        }
        Product product = productOptional.get();
        product.setStatus(Status.INACTIVE);
        productRepository.save(product);
        return messageBuilder.buildSuccessMessage(getProductResponse(product));
    }

    public RespMessage updateProduct(Long id, ProductRequest request) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"product"}, "Product not found");
        }
        Product product = productOptional.get();
        if (request.getName() != null && !request.getName().isEmpty()) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategoryId() > 0) {
            Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
            if (categoryOptional.isEmpty()) {
                throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"categoryId"}, "Category not found");
            }
            product.setCategory(categoryOptional.get());
        }
        if (request.getBrandId() > 0) {
            Optional<Brand> brandOptional = brandRepository.findById(request.getBrandId());
            if (brandOptional.isEmpty()) {
                throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"brandId"}, "Brand not found");
            }
            product.setBrand(brandOptional.get());
        }
        productRepository.save(product);
        return messageBuilder.buildSuccessMessage(getProductResponse(product));
    }

    public RespMessage getAllTypeProduct() {
        List<TypeProduct> typeProducts = typeProductRepository.findAll().stream()
                .filter(typeProduct -> typeProduct.getStatus() == Status.ACTIVE).toList();
        return messageBuilder.buildSuccessMessage(typeProducts);
    }

    public RespMessage uploadImage(Long id, MultipartFile file) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"product"}, "Product not found");
        }
        Product product = productOptional.get();
        try {
            Map<String, Object> data = cloudinaryService.upload(file, "Product");
            String url = (String) data.get("secure_url");
            Image image = new Image();
            image.setUrl(url);
            image.setProduct(product);
            imageRepository.save(image);
            return messageBuilder.buildSuccessMessage(getProductResponse(product));
        } catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when upload image");
        }

    }

    public ProductResponse getProductResponse(Product product) {
        try {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setDescription(product.getDescription());
            productResponse.setCategory(product.getCategory());
            productResponse.setBrand(product.getBrand());

            List<Image> images = imageRepository.findByProduct(product);
            productResponse.setImages(images);

            List<Review> reviews = reviewRepository.findByProductId(product.getId());
            double rating = 0;
            int totalReview = 0;

            if (!reviews.isEmpty()) {
                rating = reviews.stream().mapToDouble(Review::getRating).average().getAsDouble();
                totalReview = reviews.size();
            }
            productResponse.setRating(rating);
            productResponse.setTotalReview(totalReview);

            Integer totalSold = orderItemRepository.findTotalSold(product.getId())
                    .orElse(0);
            productResponse.setTotalSold(totalSold);

            Double maxPrice = productRepository.maxPrice(product.getId())
                    .orElse(0.0);
            productResponse.setMaxPrice(maxPrice);

            Double minPrice = productRepository.minPrice(product.getId())
                    .orElse(0.0);
            productResponse.setMinPrice(minPrice);

            return productResponse;
        }
        catch (Exception e) {
            throw new CoffeeShopException(Constant.SYSTEM_ERROR, new Object[]{e.getMessage()}, "Error when get product response");
        }
    }

    public RespMessage deleteImage(Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"image"}, "Image not found");
        }
        Image image = imageOptional.get();
        imageRepository.delete(image);
        cloudinaryService.delete(image.getUrl());
        ProductResponse productResponse = getProductResponse(image.getProduct());

        return messageBuilder.buildSuccessMessage(productResponse);
    }
}
