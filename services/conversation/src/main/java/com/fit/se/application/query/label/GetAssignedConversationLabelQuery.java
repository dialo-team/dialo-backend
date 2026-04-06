package com.yourcompany.conversationservice.application.query.label;

import com.yourcompany.conversationservice.application.common.query.Query;

public record GetAssignedConversationLabelQuery(String conversationId, Long actorUserId) implements Query {
}
