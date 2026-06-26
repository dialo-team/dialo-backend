package com.fit.se.event.consumer;

import com.fit.se.event.model.UserCreatedEvent;
import com.fit.se.user.application.command.sync.SyncUserCommand;
import com.fit.se.user.application.command.sync.SyncUserCommandHandler;
import com.fit.se.event.producer.UserProvisioningEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer {
    private final SyncUserCommandHandler syncCommandHandler;
    private final UserProvisioningEventPublisher userProvisioningEventPublisher;

    @RabbitListener(queues = "user.exchange.queue")
    public void handle(UserCreatedEvent event) {
        syncCommandHandler.execute(SyncUserCommand.builder()
                .userId(event.userId())
                .phone(event.phone())
                .build());
        userProvisioningEventPublisher.publishProvisioned(event.userId(), event.phone());
    }
}
