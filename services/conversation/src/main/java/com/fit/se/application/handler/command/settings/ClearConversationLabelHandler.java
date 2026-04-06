package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.ClearConversationLabelCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class ClearConversationLabelHandler {
    public ConversationSettingsResult handle(ClearConversationLabelCommand command) {
        throw new UnsupportedOperationException("Implement use case: ClearConversationLabelHandler");
    }
}
