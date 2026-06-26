package com.fit.se.event.producer;

import com.fit.se.event.model.UserProfileProvisionedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQUserProvisioningEventPublisher implements UserProvisioningEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishProvisioned(String userId, String phone) {
        rabbitTemplate.convertAndSend(
                "user.exchange",
                "user.profile.provisioned",
                new UserProfileProvisionedEvent(userId, phone)
        );
    }
}
