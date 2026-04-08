package com.fit.se.application.command.membership;

import com.fit.se.application.common.command.Command;

public record TransferOwnershipCommand(String conversationId, Long actorUserId, Long nextOwnerUserId) implements Command {
}
