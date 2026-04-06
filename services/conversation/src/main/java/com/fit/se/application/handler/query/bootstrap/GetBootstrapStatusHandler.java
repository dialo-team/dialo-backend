package com.yourcompany.conversationservice.application.handler.query.bootstrap;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.bootstrap.GetBootstrapStatusQuery;
import com.yourcompany.conversationservice.application.result.bootstrap.BootstrapStatusResult;

@QueryHandler
public class GetBootstrapStatusHandler {
    public BootstrapStatusResult handle(GetBootstrapStatusQuery query) {
        throw new UnsupportedOperationException("Implement query: GetBootstrapStatusHandler");
    }
}
