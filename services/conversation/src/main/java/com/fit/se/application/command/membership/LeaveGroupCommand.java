package com.yourcompany.conversationservice.application.command.membership;

import com.yourcompany.conversationservice.application.common.command.Command;

public record LeaveGroupCommand(String conversationId, Long actorUserId, Long nextOwnerUserId) implements Command {
}
