package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.model.Brand;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final MessageBuilder messageBuilder;

    public RespMessage getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        return messageBuilder.buildSuccessMessage(brands);
    }
}
