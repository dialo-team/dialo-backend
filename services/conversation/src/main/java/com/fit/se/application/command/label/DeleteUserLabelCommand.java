package com.yourcompany.conversationservice.application.command.label;

import com.yourcompany.conversationservice.application.common.command.Command;

public record DeleteUserLabelCommand(Long actorUserId, String labelId) implements Command {
}
