package com.fit.se.application.handler.command.conversation;

import com.fit.se.application.command.conversation.CreateSystemConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateSystemConversationHandler {
    public ConversationCreatedResult handle(CreateSystemConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateSystemConversationHandler");
    }
}
