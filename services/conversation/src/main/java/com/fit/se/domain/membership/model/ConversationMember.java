package com.fit.se.domain.membership.model;

import java.util.UUID;

public record ConversationMember(UUID conversationId, Long userId, MemberRole role, MembershipStatus status) {
}
