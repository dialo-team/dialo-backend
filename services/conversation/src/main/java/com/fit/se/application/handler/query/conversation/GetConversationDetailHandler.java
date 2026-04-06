package com.yourcompany.conversationservice.application.handler.query.conversation;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.conversation.GetConversationDetailQuery;
import com.yourcompany.conversationservice.application.result.conversation.ConversationDetailResult;

@QueryHandler
public class GetConversationDetailHandler {
    public ConversationDetailResult handle(GetConversationDetailQuery query) {
        throw new UnsupportedOperationException("Implement query: GetConversationDetailHandler");
    }
}
