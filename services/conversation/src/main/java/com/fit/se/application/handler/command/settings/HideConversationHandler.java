package com.fit.se.application.handler.command.settings;

import com.fit.se.application.command.settings.HideConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class HideConversationHandler {
    public ConversationSettingsResult handle(HideConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: HideConversationHandler");
    }
}
