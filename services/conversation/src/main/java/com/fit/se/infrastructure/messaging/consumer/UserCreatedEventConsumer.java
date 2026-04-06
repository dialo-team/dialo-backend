package com.fit.se.infrastructure.messaging.consumer;

import com.fit.se.application.port.input.bootstrap.BootstrapUseCase;
import com.fit.se.infrastructure.messaging.dto.UserCreatedEventMessage;
import com.fit.se.infrastructure.messaging.mapper.MessagingEventMapper;
import com.fit.se.infrastructure.messaging.topology.UserCreatedTopology;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventConsumer {

    private final BootstrapUseCase bootstrapUseCase;
    private final MessagingEventMapper mapper = new MessagingEventMapper();

    public UserCreatedEventConsumer(BootstrapUseCase bootstrapUseCase) {
        this.bootstrapUseCase = bootstrapUseCase;
    }

    @RabbitListener(queues = UserCreatedTopology.USER_CREATED_QUEUE)
    public void handle(UserCreatedEventMessage event) {
        bootstrapUseCase.bootstrap(mapper.toBootstrapCommand(event));
    }
}
