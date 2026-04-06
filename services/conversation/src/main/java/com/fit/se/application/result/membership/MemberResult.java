package com.yourcompany.conversationservice.application.result.membership;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record MemberResult(Long userId, String role, String status) implements QueryResult {
}
