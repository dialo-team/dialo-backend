package com.yourcompany.conversationservice.application.command.label;

import com.yourcompany.conversationservice.application.common.command.Command;

public record ChangeUserLabelColorCommand(Long actorUserId, String labelId, String color) implements Command {
}
