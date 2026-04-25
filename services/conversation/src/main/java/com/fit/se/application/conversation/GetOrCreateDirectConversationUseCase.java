package com.fit.se.application.conversation;

import com.fit.se.domain.direct.DirectConversation;

public class GetOrCreateDirectConversationUseCase {
    private final EnsureDirectConversationUseCase ensureDirectConversationUseCase;

    public GetOrCreateDirectConversationUseCase(
            EnsureDirectConversationUseCase ensureDirectConversationUseCase
    ) {
        this.ensureDirectConversationUseCase = ensureDirectConversationUseCase;
    }

    public DirectConversation execute(GetOrCreateDirectConversationQuery query) {
        return ensureDirectConversationUseCase.execute(
                query.userId1(),
                query.userId2()
        );
    }
}