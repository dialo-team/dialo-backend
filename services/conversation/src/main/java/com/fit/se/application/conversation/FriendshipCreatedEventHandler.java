package com.fit.se.application.conversation;


public class FriendshipCreatedEventHandler {
    private final EnsureDirectConversationUseCase ensureDirectConversationUseCase;

    public FriendshipCreatedEventHandler(
            EnsureDirectConversationUseCase ensureDirectConversationUseCase
    ) {
        this.ensureDirectConversationUseCase = ensureDirectConversationUseCase;
    }

    public void handle(FriendshipCreatedEvent event) {
        ensureDirectConversationUseCase.execute(
                event.userId1(),
                event.userId2()
        );
    }
}