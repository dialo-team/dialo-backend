package com.fit.se.application.handler.query.conversation;

import com.fit.se.application.common.annotation.QueryHandler;
import com.fit.se.application.query.conversation.GetConversationSettingsQuery;
import com.fit.se.application.result.settings.ConversationSettingsResult;

@QueryHandler
public class GetConversationSettingsHandler {
    public ConversationSettingsResult handle(GetConversationSettingsQuery query) {
        throw new UnsupportedOperationException("Implement query: GetConversationSettingsHandler");
    }
}
