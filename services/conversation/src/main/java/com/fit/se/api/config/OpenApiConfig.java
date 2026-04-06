package com.fit.se.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI conversationServiceOpenApi() {
        return new OpenAPI().info(new Info().title("Conversation Service API").version("v1").description("REST API for conversation-service"));
    }
}
