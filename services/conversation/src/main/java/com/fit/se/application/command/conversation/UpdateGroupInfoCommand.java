package com.yourcompany.conversationservice.application.command.conversation;

import com.yourcompany.conversationservice.application.common.command.Command;

public record UpdateGroupInfoCommand(String conversationId, Long actorUserId, String groupName, String avatarUrl) implements Command {
}
