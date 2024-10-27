package com.ptit.coffee_shop.config;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.enums.UserStatusEnum;
import com.ptit.coffee_shop.exception.CoffeeShopException;
import com.ptit.coffee_shop.model.Role;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.repository.RoleRepository;
import com.ptit.coffee_shop.common.enums.RoleEnum;
import com.ptit.coffee_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
            }
        }

        if (!userRepository.existsUserByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword);
            Role role = roleRepository.getRoleByName(RoleEnum.ROLE_ADMIN)
                    .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"DataInitializer.run"}, "Role Admin not found"));
            admin.setRole(role);
            admin.setStatus(UserStatusEnum.ACTIVE);

            userRepository.save(admin);
        }
    }
}
