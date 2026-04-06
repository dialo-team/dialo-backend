package com.yourcompany.conversationservice.application.command.settings;

import com.yourcompany.conversationservice.application.common.command.Command;

public record AssignConversationLabelCommand(String conversationId, Long actorUserId, String labelId) implements Command {
}
