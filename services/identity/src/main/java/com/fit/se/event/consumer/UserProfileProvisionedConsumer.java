package com.fit.se.event.consumer;

import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.event.model.UserProfileProvisionedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileProvisionedConsumer {
    private final AccountRepository accountRepository;

    @RabbitListener(queues = "identity.user.profile.provisioned.queue")
    public void handle(UserProfileProvisionedEvent event) {
        accountRepository.markProfileProvisioned(event.userId());
    }
}
