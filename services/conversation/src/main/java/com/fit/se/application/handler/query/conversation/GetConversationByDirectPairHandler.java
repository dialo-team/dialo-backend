package com.yourcompany.conversationservice.application.handler.query.conversation;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.conversation.GetConversationByDirectPairQuery;
import com.yourcompany.conversationservice.application.result.conversation.ConversationDetailResult;

@QueryHandler
public class GetConversationByDirectPairHandler {
    public ConversationDetailResult handle(GetConversationByDirectPairQuery query) {
        throw new UnsupportedOperationException("Implement query: GetConversationByDirectPairHandler");
    }
}
