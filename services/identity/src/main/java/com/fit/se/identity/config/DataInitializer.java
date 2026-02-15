package com.fit.se.identity.config;

import com.fit.se.identity.role.Role;
import com.fit.se.identity.role.RoleRepository;
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
            createIfNotExists("ROLE_USER");
            createIfNotExists("ROLE_ADMIN");
        };
    }

    private void createIfNotExists(String name) {
        if (!roleRepo.existsByName(name)) {
            Role role = new Role();
            role.setName(name);
            roleRepo.save(role);
        }
    }
}
