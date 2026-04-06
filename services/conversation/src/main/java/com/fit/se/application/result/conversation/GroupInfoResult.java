package com.yourcompany.conversationservice.application.result.conversation;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record GroupInfoResult(String groupName, String avatarUrl) implements QueryResult {
}
