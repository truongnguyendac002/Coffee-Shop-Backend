package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.request.UserRequest;
import com.ptit.coffee_shop.payload.response.ProfileResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final MessageBuilder messageBuilder;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;



    public RespMessage getProfile() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty())
            throw new CoffeeShopException(Constant.UNAUTHORIZED, null, "User not found by email: " + userEmail + "get from token!");
        User user = userOptional.get();
        ProfileResponse profileResponse = user.toProfileResponse();
        return messageBuilder.buildSuccessMessage(profileResponse);
    }

    public RespMessage updateProfile(UserRequest userRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty())
            throw new CoffeeShopException(Constant.UNAUTHORIZED, null, "User not found by email: " + userEmail + "get from token!");
        User user = userOptional.get();

        if (userRequest.getName() != null)
            user.setName(userRequest.getName());
        if (userRequest.getPhone() != null)
            user.setPhone(userRequest.getPhone());
        if (userRequest.getProfileImg() != null)
            user.setProfile_img(userRequest.getProfileImg());


        if (userRequest.getPassword() != null && userRequest.getConfirmPassword() != null
            && userRequest.getPassword().equals(userRequest.getConfirmPassword()))
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        userRepository.save(user);
        return messageBuilder.buildSuccessMessage(user.toProfileResponse());

    }

    public RespMessage updateAvatar(MultipartFile file) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty())
            throw new CoffeeShopException(Constant.UNAUTHORIZED, null, "User not found by email: " + userEmail + "get from token!");
        User user = userOptional.get();
        Map<String, Object> data = cloudinaryService.upload(file, "Avatar");
        System.out.println(data);
        String url = (String) data.get("secure_url");
        System.out.println();
        user.setProfile_img(url);
        userRepository.save(user);
        return messageBuilder.buildSuccessMessage(user.toProfileResponse());
    }
}
