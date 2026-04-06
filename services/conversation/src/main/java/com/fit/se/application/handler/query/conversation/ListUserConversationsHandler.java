package com.yourcompany.conversationservice.application.handler.query.conversation;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.conversation.ListUserConversationsQuery;
import com.yourcompany.conversationservice.application.result.conversation.ConversationSummaryResult;

@QueryHandler
public class ListUserConversationsHandler {
    public ConversationSummaryResult handle(ListUserConversationsQuery query) {
        throw new UnsupportedOperationException("Implement query: ListUserConversationsHandler");
    }
}
