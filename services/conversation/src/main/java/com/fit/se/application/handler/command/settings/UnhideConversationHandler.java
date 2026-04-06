package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.UnhideConversationCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class UnhideConversationHandler {
    public ConversationSettingsResult handle(UnhideConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: UnhideConversationHandler");
    }
}
