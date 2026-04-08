package com.fit.se.application.handler.command.conversation;

import com.fit.se.application.command.conversation.CreateSelfConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateSelfConversationHandler {
    public ConversationCreatedResult handle(CreateSelfConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateSelfConversationHandler");
    }
}
