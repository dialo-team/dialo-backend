package com.yourcompany.conversationservice.application.handler.command.settings;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.settings.ChangeConversationAliasCommand;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@CommandHandler
public class ChangeConversationAliasHandler {
    public ConversationSettingsResult handle(ChangeConversationAliasCommand command) {
        throw new UnsupportedOperationException("Implement use case: ChangeConversationAliasHandler");
    }
}
