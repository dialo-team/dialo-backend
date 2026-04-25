package com.fit.se.application.conversation;

import java.util.List;

public record CreateGroupConversationCommand(
        String creatorId,
        String groupName,
        List<String> memberIds
) {
}