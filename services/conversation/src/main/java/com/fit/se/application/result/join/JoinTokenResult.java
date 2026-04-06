package com.yourcompany.conversationservice.application.result.join;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record JoinTokenResult(String conversationId, String joinToken, String joinUrl) implements QueryResult {
}
