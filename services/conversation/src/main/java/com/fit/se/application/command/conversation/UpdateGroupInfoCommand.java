package com.fit.se.application.command.conversation;

import com.fit.se.application.common.command.Command;

public record UpdateGroupInfoCommand(String conversationId, Long actorUserId, String groupName, String avatarUrl) implements Command {
}
