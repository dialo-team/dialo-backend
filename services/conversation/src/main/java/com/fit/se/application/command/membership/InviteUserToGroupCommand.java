package com.fit.se.application.command.membership;

import com.fit.se.application.common.command.Command;

public record InviteUserToGroupCommand(String conversationId, Long actorUserId, Long invitedUserId) implements Command {
}
