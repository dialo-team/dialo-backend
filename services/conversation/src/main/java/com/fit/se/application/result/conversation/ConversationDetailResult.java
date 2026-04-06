package com.yourcompany.conversationservice.application.result.conversation;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record ConversationDetailResult(String conversationId, String type, String status, String displayName, String avatarUrl, boolean approvalRequired, String joinToken) implements QueryResult {
}
