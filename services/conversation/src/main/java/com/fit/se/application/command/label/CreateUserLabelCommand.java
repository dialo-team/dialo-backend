package com.fit.se.application.command.label;

import com.fit.se.application.common.command.Command;

public record CreateUserLabelCommand(Long actorUserId, String name, String color) implements Command {
}
