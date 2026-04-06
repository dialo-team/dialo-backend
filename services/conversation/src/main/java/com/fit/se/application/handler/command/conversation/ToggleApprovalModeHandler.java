package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.ToggleApprovalModeCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationDetailResult;

@CommandHandler
public class ToggleApprovalModeHandler {
    public ConversationDetailResult handle(ToggleApprovalModeCommand command) {
        throw new UnsupportedOperationException("Implement use case: ToggleApprovalModeHandler");
    }
}
