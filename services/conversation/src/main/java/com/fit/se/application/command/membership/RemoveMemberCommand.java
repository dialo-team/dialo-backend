package com.fit.se.application.command.membership;

import com.fit.se.application.common.command.Command;

public record RemoveMemberCommand(String conversationId, Long actorUserId, Long memberUserId) implements Command {
}
