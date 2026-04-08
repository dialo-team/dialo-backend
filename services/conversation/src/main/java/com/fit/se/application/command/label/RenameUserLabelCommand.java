package com.fit.se.application.command.label;

import com.fit.se.application.common.command.Command;

public record RenameUserLabelCommand(Long actorUserId, String labelId, String name) implements Command {
}
