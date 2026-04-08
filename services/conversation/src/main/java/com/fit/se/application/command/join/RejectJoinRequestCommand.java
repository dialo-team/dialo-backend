package com.fit.se.application.command.join;

import com.fit.se.application.common.command.Command;

public record RejectJoinRequestCommand(String joinRequestId, Long actorUserId, String reason) implements Command {
}
