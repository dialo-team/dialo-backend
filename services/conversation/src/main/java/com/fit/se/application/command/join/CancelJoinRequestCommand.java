package com.fit.se.application.command.join;

import com.fit.se.application.common.command.Command;

public record CancelJoinRequestCommand(String joinRequestId, Long actorUserId) implements Command {
}
