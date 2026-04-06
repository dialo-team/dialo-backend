package com.fit.se.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.fit.se.infrastructure.persistence.repository.jpa")
public class JpaConfig {
}
