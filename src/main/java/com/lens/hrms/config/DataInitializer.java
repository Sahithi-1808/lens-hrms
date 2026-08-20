package com.lens.hrms.config;

import com.lens.hrms.entity.*;
import com.lens.hrms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedUsers(UserRepository repo, PasswordEncoder encoder,
                                @Value("${app.seed-demo-users:false}") boolean seed) {
        return args -> {
            if (!seed) return;
            seed(repo, encoder, "admin@lens.com", "Admin@123", Role.ADMIN);
            seed(repo, encoder, "hr@lens.com", "Hr@12345", Role.HR);
            seed(repo, encoder, "employee@lens.com", "Employee@123", Role.EMPLOYEE);
        };
    }
    private void seed(UserRepository repo, PasswordEncoder encoder, String email, String password, Role role) {
        if (!repo.existsByEmail(email))
            repo.save(new User(email, encoder.encode(password), role));
    }
}
