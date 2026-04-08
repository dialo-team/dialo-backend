package com.fit.se.application.handler.command.settings;

import com.fit.se.application.command.settings.UnmuteConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class UnmuteConversationHandler {
    public ConversationSettingsResult handle(UnmuteConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: UnmuteConversationHandler");
    }
}
