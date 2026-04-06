package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.HideConversationCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class HideConversationHandler {
    public ConversationSettingsResult handle(HideConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: HideConversationHandler");
    }
}
