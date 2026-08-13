package com.example.back.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class DefaultUserSeederTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateDefaultAdminWhenNoUsersExist() {
        DefaultUserSeeder seeder = new DefaultUserSeeder(userRepository, passwordEncoder);

        seeder.ensureDefaultAdminExists();

        User admin = userRepository.findByEmail("admin@brainbooster.local").orElseThrow();
        assertThat(admin.getRole()).isEqualTo("ADMIN");
        assertThat(passwordEncoder.matches("Admin123!", admin.getPasswordHash())).isTrue();
    }
}
