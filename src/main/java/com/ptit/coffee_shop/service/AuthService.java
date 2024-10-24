package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.payload.request.LoginRequest;
import com.ptit.coffee_shop.payload.request.RegisterRequest;
import com.ptit.coffee_shop.model.Role;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.response.LoginResponse;
import com.ptit.coffee_shop.repository.RoleRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import com.ptit.coffee_shop.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponse response = jwtTokenProvider.generateToken(authentication);

        return response;
    }

    public String register(RegisterRequest registerRequest) {
        String emailDto = registerRequest.getEmail();
        String passwordDto = registerRequest.getPassword();
        if (userRepository.existsUserByEmail(emailDto)){
            return "Email is already exists!.";
        }
        if (!passwordDto.equals(registerRequest.getConfirmPassword())){
            return "Password and Confirm Password must be the same!.";
        }
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        if (roleOptional.isEmpty()){
            return "Role not found!.";
        }
        User user = User.builder()
                .email(emailDto)
                .password(passwordEncoder.encode(passwordDto))
                .role(roleOptional.get())
                .build();
        userRepository.save(user);
        return "User registered successfully!.";
    }
}
