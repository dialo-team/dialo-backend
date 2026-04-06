package com.yourcompany.conversationservice.application.handler.query.membership;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.membership.ListConversationMembersQuery;
import com.yourcompany.conversationservice.application.result.membership.MemberResult;

@QueryHandler
public class ListConversationMembersHandler {
    public MemberResult handle(ListConversationMembersQuery query) {
        throw new UnsupportedOperationException("Implement query: ListConversationMembersHandler");
    }
}
