package com.fit.se.application.query.label;

import com.fit.se.application.common.query.Query;

public record GetAssignedConversationLabelQuery(String conversationId, Long actorUserId) implements Query {
}
