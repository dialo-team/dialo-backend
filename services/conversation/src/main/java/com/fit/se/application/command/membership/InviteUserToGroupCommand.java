package com.yourcompany.conversationservice.application.command.membership;

import com.yourcompany.conversationservice.application.common.command.Command;

public record InviteUserToGroupCommand(String conversationId, Long actorUserId, Long invitedUserId) implements Command {
}
