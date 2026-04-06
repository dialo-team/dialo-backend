package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.CreateSelfConversationCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateSelfConversationHandler {
    public ConversationCreatedResult handle(CreateSelfConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateSelfConversationHandler");
    }
}
