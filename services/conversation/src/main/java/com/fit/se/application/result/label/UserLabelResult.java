package com.yourcompany.conversationservice.application.result.label;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record UserLabelResult(String labelId, String name, String color, boolean defaultLabel, boolean deletable) implements QueryResult {
}
