package com.fit.se.application.command.label;

import com.fit.se.application.common.command.Command;

public record DeleteUserLabelCommand(Long actorUserId, String labelId) implements Command {
}
