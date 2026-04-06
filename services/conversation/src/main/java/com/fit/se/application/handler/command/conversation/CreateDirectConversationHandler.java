package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.CreateDirectConversationCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateDirectConversationHandler {
    public ConversationCreatedResult handle(CreateDirectConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateDirectConversationHandler");
    }
}
