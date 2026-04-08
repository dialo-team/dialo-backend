package com.fit.se.application.handler.command.conversation;

import com.fit.se.application.command.conversation.CreateDirectConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class CreateDirectConversationHandler {
    public ConversationCreatedResult handle(CreateDirectConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateDirectConversationHandler");
    }
}
