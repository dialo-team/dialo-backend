package com.fit.se.application.command.label;

import com.fit.se.application.common.command.Command;

public record ChangeUserLabelColorCommand(Long actorUserId, String labelId, String color) implements Command {
}
