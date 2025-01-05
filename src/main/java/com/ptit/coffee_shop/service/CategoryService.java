package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Category;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    final CategoryRepository categoryRepository;
    final MessageBuilder messageBuilder;
    final CloudinaryService cloudinaryService;

    public RespMessage getAllCategories() {
        List<Category> categories = categoryRepository.findAllCategories()
                .stream().filter(category -> category.getStatus().equals(Status.ACTIVE)).toList();
        return messageBuilder.buildSuccessMessage(categories);
    }

    public RespMessage getCategoryById(long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            if (category.get().getStatus().equals(Status.INACTIVE)) {
                throw new CoffeeShopException(Constant.NOT_FOUND, null, "Category not active");
            }
            return messageBuilder.buildSuccessMessage(category.get());
        } else {
            throw new CoffeeShopException(Constant.NOT_FOUND, null, "Category not found");
        }
    }

    public RespMessage addCategory(String name, String description, MultipartFile imageFile) {
        if (name == null || name.trim().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Category name must be not null");
        }
        if (categoryRepository.findByName(name).isPresent()) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Category name is duplicate");
        }
        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description.trim());
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinaryService.upload(imageFile, "categories");

                String imageUrl = (String) uploadResult.get("secure_url");
                category.setDefaultImageUrl(imageUrl);
            } catch (Exception e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, null, "Failed to upload image file");
            }
        }
        try {
            categoryRepository.save(category);
            return messageBuilder.buildSuccessMessage(category);
        } catch (Exception e) {
            throw new RuntimeException("Category could not be saved");
        }
    }

    public RespMessage updateCategory(Long id, String name, String description, MultipartFile imageFile) {
        if (name == null || name.trim().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Category name must be not null");
        }
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if ( !existingCategory.getName().equals(name) && categoryRepository.findByName(name).isPresent()) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Category name is duplicate");
        }
        existingCategory.setName(name.trim());
        existingCategory.setDescription(description.trim());
        existingCategory.setStatus(Status.ACTIVE);

        if (existingCategory.getDefaultImageUrl() != null && !existingCategory.getDefaultImageUrl().isEmpty()) {
            try {
                cloudinaryService.delete(existingCategory.getDefaultImageUrl());
            } catch (Exception e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, null, "Image could not be deleted");
            }
        }

        existingCategory.setDefaultImageUrl(null);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinaryService.upload(imageFile, "categories");
                String imageUrl = (String) uploadResult.get("secure_url");
                existingCategory.setDefaultImageUrl(imageUrl);
                System.out.println(imageUrl);
            } catch (Exception e) {
                throw new CoffeeShopException(Constant.SYSTEM_ERROR, null, "Failed to upload image file");
            }
        }
        try {
            categoryRepository.save(existingCategory);
            return messageBuilder.buildSuccessMessage(existingCategory);
        } catch (Exception e) {
            throw new RuntimeException("Category could not be saved");
        }
    }

    public RespMessage deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getDefaultImageUrl() != null) {
            cloudinaryService.delete(category.getDefaultImageUrl());
        }
        category.setStatus(Status.INACTIVE);
        try {
            categoryRepository.save(category);
            return messageBuilder.buildSuccessMessage(category.getId());
        } catch (Exception e) {
            throw new RuntimeException("Category could not be deleted");
        }
    }
}
