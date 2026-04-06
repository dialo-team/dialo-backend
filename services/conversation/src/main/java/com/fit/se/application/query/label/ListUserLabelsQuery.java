package com.yourcompany.conversationservice.application.query.label;

import com.yourcompany.conversationservice.application.common.query.Query;

public record ListUserLabelsQuery(Long actorUserId) implements Query {
}
