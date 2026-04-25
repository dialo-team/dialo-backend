package com.fit.se.infrastructure.messaging;

import com.fit.se.application.conversation.EnsureDirectConversationUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FriendAcceptedEventConsumer {

    private final EnsureDirectConversationUseCase ensureDirectConversationUseCase;

    public FriendAcceptedEventConsumer(
            EnsureDirectConversationUseCase ensureDirectConversationUseCase
    ) {
        this.ensureDirectConversationUseCase = ensureDirectConversationUseCase;
    }

    @RabbitListener(queues = "${messaging.queue.friend-accepted}")
    public void consume(FriendAcceptedEvent event) {
        if (event == null || event.payload() == null) {
            return;
        }

        ensureDirectConversationUseCase.execute(
                event.payload().user1Id(),
                event.payload().user2Id()
        );
    }
}