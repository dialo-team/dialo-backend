package com.yourcompany.conversationservice.application.result.label;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record AssignedLabelResult(String conversationId, String labelId, String labelName, String color) implements QueryResult {
}
