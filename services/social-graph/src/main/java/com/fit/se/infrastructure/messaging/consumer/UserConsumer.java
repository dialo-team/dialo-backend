package com.fit.se.infrastructure.messaging.consumer;

import com.fit.se.application.user.command.sync.SyncUserCommand;
import com.fit.se.application.user.command.sync.SyncUserCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer {
    private final SyncUserCommandHandler syncCommandHandler;

    @RabbitListener(queues = "user.exchange.queue")
    public void handle(UserCreatedEvent event) {
        System.out.println("Received user: " + event.userId());
        syncCommandHandler.execute(SyncUserCommand.builder()
                        .userId(event.userId())
                        .phone(event.phone())
                .build());
    }
}
