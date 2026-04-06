package com.yourcompany.conversationservice.application.handler.query.label;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.label.ListUserLabelsQuery;
import com.yourcompany.conversationservice.application.result.label.UserLabelResult;

@QueryHandler
public class ListUserLabelsHandler {
    public UserLabelResult handle(ListUserLabelsQuery query) {
        throw new UnsupportedOperationException("Implement query: ListUserLabelsHandler");
    }
}
