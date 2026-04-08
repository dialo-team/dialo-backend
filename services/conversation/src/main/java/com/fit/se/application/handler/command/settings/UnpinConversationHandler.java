package com.fit.se.application.handler.command.settings;

import com.fit.se.application.command.settings.UnpinConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class UnpinConversationHandler {
    public ConversationSettingsResult handle(UnpinConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: UnpinConversationHandler");
    }
}
