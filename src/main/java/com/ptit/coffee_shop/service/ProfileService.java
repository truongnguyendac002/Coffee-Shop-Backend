package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.response.ProfileResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final MessageBuilder messageBuilder;


    public RespMessage getProfile() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty())
            throw new CoffeeShopException(Constant.UNAUTHORIZED, null, "User not found by email: " + userEmail + "get from token!");
        User user = userOptional.get();
        ProfileResponse profileResponse = user.toProfileResponse();
        return messageBuilder.buildSuccessMessage(profileResponse);
    }
}
