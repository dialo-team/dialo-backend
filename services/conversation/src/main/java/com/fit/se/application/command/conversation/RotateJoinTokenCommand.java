package com.yourcompany.conversationservice.application.command.conversation;

import com.yourcompany.conversationservice.application.common.command.Command;

public record RotateJoinTokenCommand(String conversationId, Long actorUserId) implements Command {
}
