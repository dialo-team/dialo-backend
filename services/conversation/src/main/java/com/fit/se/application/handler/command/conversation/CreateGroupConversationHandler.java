package com.fit.se.application.handler.command.conversation;

import com.fit.se.application.command.conversation.CreateGroupConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateGroupConversationHandler {
    public ConversationCreatedResult handle(CreateGroupConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateGroupConversationHandler");
    }
}
