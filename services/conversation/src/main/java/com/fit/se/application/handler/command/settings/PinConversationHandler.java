package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.PinConversationCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class PinConversationHandler {
    public ConversationSettingsResult handle(PinConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: PinConversationHandler");
    }
}
