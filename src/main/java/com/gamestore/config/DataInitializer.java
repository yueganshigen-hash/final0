package com.gamestore.config;

import com.gamestore.entity.User;
import com.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("test1")) {
            User admin = new User();
            admin.setUsername("test1");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("test1@gamestore.com");
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            admin.setMerchantStatus(User.MerchantStatus.PENDING);
            userRepository.save(admin);
            System.out.println("管理员 test1 已创建");
        }
    }
}