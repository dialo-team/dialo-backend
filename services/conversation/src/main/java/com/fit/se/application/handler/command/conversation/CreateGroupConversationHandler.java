package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.CreateGroupConversationCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateGroupConversationHandler {
    public ConversationCreatedResult handle(CreateGroupConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateGroupConversationHandler");
    }
}
