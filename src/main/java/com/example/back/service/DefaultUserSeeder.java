package com.example.back.service;

import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserSeeder implements ApplicationRunner {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@brainbooster.local";
    private static final String DEFAULT_ADMIN_NAME = "Admin BrainBooster";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin123!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureDefaultAdminExists();
    }

    public void ensureDefaultAdminExists() {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User(
                DEFAULT_ADMIN_NAME,
                DEFAULT_ADMIN_EMAIL,
                passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD)
        );
        admin.setRole("ADMIN");
        userRepository.save(admin);
    }
}
