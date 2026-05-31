package com.fit.se.common.config;

import org.axonframework.common.infra.ComponentDescriptor;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.commandhandling.gateway.CommandResult;
import org.axonframework.messaging.core.Metadata;
import org.axonframework.messaging.core.unitofwork.ProcessingContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {
    @Bean
    public CommandGateway commandGateway() {
        return new CommandGateway() {
            @Override
            public @NonNull CommandResult send(@NonNull Object command, @NonNull Metadata metadata, @Nullable ProcessingContext context) {
                return null;
            }

            @Override
            public void describeTo(@NonNull ComponentDescriptor descriptor) {

            }
        };
    }
}
