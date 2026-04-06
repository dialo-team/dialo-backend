package com.yourcompany.conversationservice.application.command.label;

import com.yourcompany.conversationservice.application.common.command.Command;

public record CreateUserLabelCommand(Long actorUserId, String name, String color) implements Command {
}
