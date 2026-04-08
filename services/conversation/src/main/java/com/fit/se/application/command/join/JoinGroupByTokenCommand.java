package com.fit.se.application.command.join;

import com.fit.se.application.common.command.Command;

public record JoinGroupByTokenCommand(Long actorUserId, String joinToken, String joinMethod) implements Command {
}
