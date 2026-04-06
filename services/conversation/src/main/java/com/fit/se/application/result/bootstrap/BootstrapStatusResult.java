package com.yourcompany.conversationservice.application.result.bootstrap;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record BootstrapStatusResult(Long userId, boolean selfConversationCreated, boolean systemConversationCreated, boolean defaultLabelsCreated) implements QueryResult {
}
