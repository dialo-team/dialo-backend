package com.fit.se.infrastructure.config;

import com.fit.se.infrastructure.persistence.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepo;

    @Bean
    CommandLineRunner initRoles() {
        return args -> {

        };
    }
}
