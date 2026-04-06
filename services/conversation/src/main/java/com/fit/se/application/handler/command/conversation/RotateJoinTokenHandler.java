package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.RotateJoinTokenCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationDetailResult;

@CommandHandler
public class RotateJoinTokenHandler {
    public ConversationDetailResult handle(RotateJoinTokenCommand command) {
        throw new UnsupportedOperationException("Implement use case: RotateJoinTokenHandler");
    }
}
