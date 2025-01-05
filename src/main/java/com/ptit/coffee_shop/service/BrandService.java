package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Brand;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final MessageBuilder messageBuilder;

    public RespMessage getAllBrands() {
        List<Brand> brands = brandRepository.getAll()
                .stream().filter(brand -> brand.getStatus().equals(Status.ACTIVE)).toList();
        return messageBuilder.buildSuccessMessage(brands);
    }

    public RespMessage updateBrand(long id, Brand currentBrand){
        if (currentBrand.getName() == null || currentBrand.getName().trim().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"name"}, "Brand name must be not null");
        }
        Optional<Brand> brand = brandRepository.findById(id);
        if(brand.isPresent()){
            Brand brandEntity = brand.get();
            if (!brandEntity.getName().equals(currentBrand.getName()) && brandRepository.findByName(currentBrand.getName()).isPresent()) {
                throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"name"}, "Brand name is duplicate");
            }
            brandEntity.setName(currentBrand.getName());
            try {
                brandRepository.save(brandEntity);
                return messageBuilder.buildSuccessMessage(brandEntity);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.UNDEFINED, null, "Could not update brand");
            }
        } else {
            throw new CoffeeShopException(Constant.UNDEFINED, null, "Brand not found");
        }
    }

    public RespMessage deleteBrand(long id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            Brand brandToDelete = brand.get();
            brandToDelete.setStatus(Status.INACTIVE);
            try {
                brandRepository.save(brandToDelete);
                return messageBuilder.buildSuccessMessage(brandToDelete);
            } catch (CoffeeShopException e) {
                throw new CoffeeShopException(Constant.UNDEFINED, null, "Could not delete brand");
            }
        }else {
            throw new CoffeeShopException(Constant.NOT_FOUND, null, "Brand not found");
        }
    }
}
