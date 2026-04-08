package com.fit.se.application.command.join;

import com.fit.se.application.common.command.Command;

public record ApproveJoinRequestCommand(String joinRequestId, Long actorUserId) implements Command {
}
