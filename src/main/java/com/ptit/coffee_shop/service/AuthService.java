package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.RoleEnum;
import com.ptit.coffee_shop.common.enums.Status;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.payload.request.ChangePasswordDTO;
import com.ptit.coffee_shop.payload.request.LoginRequest;
import com.ptit.coffee_shop.payload.request.RegisterRequest;
import com.ptit.coffee_shop.model.Role;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.response.LoginResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.payload.response.UserDTO;
import com.ptit.coffee_shop.repository.RoleRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import com.ptit.coffee_shop.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageBuilder messageBuilder;
    private final ChatService chatService;

    // Login method
    public RespMessage login(LoginRequest loginRequest) {
        checkLoginRequest(loginRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponse response = jwtTokenProvider.generateToken(authentication);
        return messageBuilder.buildSuccessMessage(response);
    }

    private void checkLoginRequest(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"LoginRequest.Email"}, "Email must be not null");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"LoginRequest.Password"}, "Password must be not null");
        }
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"LoginRequest.Email"}, "Email not found");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"LoginRequest.Password"}, "Password not correct");
        }
        if (!userOptional.get().isEnabled()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"LoginRequest.Email"}, "User is disabled");
        }
    }

    // Register method
    @Transactional
    public RespMessage register(RegisterRequest registerRequest) {
        checkRegisterRequest(registerRequest);
        String emailDto = registerRequest.getEmail();
        String passwordDto = registerRequest.getPassword();

        Role role = roleRepository.getRoleByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"Role"}, "Role not found"));
        User user = User.builder()
                .email(emailDto)
                .password(passwordEncoder.encode(passwordDto))
                .role(role)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);
        chatService.createConversation(user.getId());
        RespMessage respMessage = messageBuilder.buildSuccessMessage(null);
        return respMessage;
    }

    public void checkRegisterRequest(RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"RegisterRequest.Email"}, "Email must be not null");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"RegisterRequest.Password"}, "Password must be not null");
        }
        if (registerRequest.getConfirmPassword() == null || registerRequest.getConfirmPassword().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"RegisterRequest.ConfirmPassword"}, "ConfirmPassword must be not null");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"RegisterRequest.Password"}, "Password and RegisterRequest.ConfirmPassword must be the same");
        }

        if (userRepository.existsUserByEmail(registerRequest.getEmail())) {
            throw new CoffeeShopException(Constant.FIELD_EXISTED, new Object[]{"RegisterRequest.Email"}, "Email is existed");
        }
    }

    public RespMessage getProfileByToken() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"User"}, "User not found when get profile by token"));

            UserDTO userDTO = UserDTO.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .roleName(user.getRole().getName().name())
                    .profile_img(user.getProfile_img())
                    .status(user.getStatus().name())
                    .id(user.getId())
                    .build();
            return messageBuilder.buildSuccessMessage(userDTO);

        }
        catch(Exception e) {
            throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"Token"}, "Token is null or not valid");
        }
    }

    public RespMessage refreshAccessToken(String refreshToken) {

        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"RefreshToken"}, "Refresh token is missing or invalid");
        }

        refreshToken = refreshToken.substring(7);

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"RefreshToken"}, "Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"User"}, "User not found for provided refresh token"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return RespMessage.builder()
                .respCode("000")
                .respDesc("Access token refreshed successfully")
                .data(Map.of(
                        "accessToken", newAccessToken,
                        "refreshToken", newRefreshToken
                ))
                .build();
    }

    public RespMessage changePassword(ChangePasswordDTO changePasswordDTO) {
        checkChangePasswordDTO(changePasswordDTO);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"User"}, "User not found when change password"));

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"ChangePasswordDTO.OldPassword"}, "Old password not correct");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
        return messageBuilder.buildSuccessMessage("Update password successfully");
    }

    private void checkChangePasswordDTO(ChangePasswordDTO changePasswordDTO) {
        if (changePasswordDTO.getOldPassword() == null || changePasswordDTO.getOldPassword().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"ChangePasswordDTO.OldPassword"}, "Old password must be not null");
        }
        if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"ChangePasswordDTO.NewPassword"}, "New password must be not null");
        }
        if (changePasswordDTO.getConfirmPassword() == null || changePasswordDTO.getConfirmPassword().isEmpty()) {
            throw new CoffeeShopException(Constant.FIELD_NOT_NULL, new Object[]{"ChangePasswordDTO.ConfirmPassword"}, "Confirm password must be not null");
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"ChangePasswordDTO.NewPassword"}, "New password and confirm password must be the same");
        }
    }
}
