package com.fit.se.application.handler.command.settings;

import com.fit.se.application.command.settings.MuteConversationCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class MuteConversationHandler {
    public ConversationSettingsResult handle(MuteConversationCommand command) {
        throw new UnsupportedOperationException("Implement use case: MuteConversationHandler");
    }
}
