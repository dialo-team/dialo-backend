package com.fit.se.application.handler.query.conversation;

import com.fit.se.application.common.annotation.QueryHandler;
import com.fit.se.application.query.conversation.ListUserConversationsQuery;
import com.fit.se.application.result.conversation.ConversationSummaryResult;

@QueryHandler
public class ListUserConversationsHandler {
    public ConversationSummaryResult handle(ListUserConversationsQuery query) {
        throw new UnsupportedOperationException("Implement query: ListUserConversationsHandler");
    }
}
