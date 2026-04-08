package com.fit.se.application.command.settings;

import com.fit.se.application.common.command.Command;

public record AssignConversationLabelCommand(String conversationId, Long actorUserId, String labelId) implements Command {
}
