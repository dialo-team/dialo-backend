package com.yourcompany.conversationservice.application.handler.command.conversation;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.conversation.UpdateGroupInfoCommand;
import com.yourcompany.conversationservice.application.result.conversation.ConversationDetailResult;

@CommandHandler
public class UpdateGroupInfoHandler {
    public ConversationDetailResult handle(UpdateGroupInfoCommand command) {
        throw new UnsupportedOperationException("Implement use case: UpdateGroupInfoHandler");
    }
}
