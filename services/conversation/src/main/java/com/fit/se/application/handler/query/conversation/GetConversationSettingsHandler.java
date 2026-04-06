package com.yourcompany.conversationservice.application.handler.query.conversation;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.conversation.GetConversationSettingsQuery;
import com.yourcompany.conversationservice.application.result.settings.ConversationSettingsResult;

@QueryHandler
public class GetConversationSettingsHandler {
    public ConversationSettingsResult handle(GetConversationSettingsQuery query) {
        throw new UnsupportedOperationException("Implement query: GetConversationSettingsHandler");
    }
}
