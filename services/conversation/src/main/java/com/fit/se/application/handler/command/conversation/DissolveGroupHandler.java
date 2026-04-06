package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.DissolveGroupCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class DissolveGroupHandler {
    public ConversationCreatedResult handle(DissolveGroupCommand command) {
        throw new UnsupportedOperationException("Implement use case: DissolveGroupHandler");
    }
}
