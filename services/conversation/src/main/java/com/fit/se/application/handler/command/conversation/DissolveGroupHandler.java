package com.fit.se.application.handler.command.conversation;

import com.fit.se.application.command.conversation.DissolveGroupCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.conversation.ConversationCreatedResult;

@CommandHandler
public class DissolveGroupHandler {
    public ConversationCreatedResult handle(DissolveGroupCommand command) {
        throw new UnsupportedOperationException("Implement use case: DissolveGroupHandler");
    }
}
