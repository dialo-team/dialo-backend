package com.yourcompany.conversationservice.application.result.conversation;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record ConversationSummaryResult(String conversationId, String type, String displayName, String status) implements QueryResult {
}
