package com.yourcompany.conversationservice.application.handler.query.join;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.join.ListPendingJoinRequestsQuery;
import com.yourcompany.conversationservice.application.result.join.JoinRequestResult;

@QueryHandler
public class ListPendingJoinRequestsHandler {
    public JoinRequestResult handle(ListPendingJoinRequestsQuery query) {
        throw new UnsupportedOperationException("Implement query: ListPendingJoinRequestsHandler");
    }
}
