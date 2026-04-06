package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.CreateSystemConversationCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateSystemConversationHandler {
    public ConversationCreatedResult handle(CreateSystemConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateSystemConversationHandler");
    }
}
