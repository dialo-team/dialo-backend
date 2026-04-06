package com.yourcompany.conversationservice.application.command.settings;

import com.yourcompany.conversationservice.application.common.command.Command;

public record UnpinConversationCommand(String conversationId, Long actorUserId) implements Command {
}
