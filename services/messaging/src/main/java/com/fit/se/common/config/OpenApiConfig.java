package com.fit.se.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI messagingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dialo Messaging API")
                        .version("v1")
                        .description("REST API cho messaging service cua Dialo")
                        .contact(new Contact().name("Dialo Backend"))
                        .license(new License().name("Internal Use")));
    }
}
