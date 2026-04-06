package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.UnpinConversationCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class UnpinConversationHandler {
    public ConversationSettingsResult handle(UnpinConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: UnpinConversationHandler");
    }
}
