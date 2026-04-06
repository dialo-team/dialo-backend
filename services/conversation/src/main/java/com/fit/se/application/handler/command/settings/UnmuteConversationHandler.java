package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.UnmuteConversationCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class UnmuteConversationHandler {
    public ConversationSettingsResult handle(UnmuteConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: UnmuteConversationHandler");
    }
}
