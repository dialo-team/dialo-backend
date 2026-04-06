package com.yourcompany.conversationservice.application.command.label;

import com.yourcompany.conversationservice.application.common.command.Command;

public record RenameUserLabelCommand(Long actorUserId, String labelId, String name) implements Command {
}
