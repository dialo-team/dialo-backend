package com.yourcompany.conversationservice.application.result.join;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record JoinRequestResult(String joinRequestId, String conversationId, Long requesterId, String joinMethod, String status) implements QueryResult {
}
