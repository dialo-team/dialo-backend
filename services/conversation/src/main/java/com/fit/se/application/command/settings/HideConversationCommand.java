package com.yourcompany.conversationservice.application.command.settings;

import com.yourcompany.conversationservice.application.common.command.Command;

public record HideConversationCommand(String conversationId, Long actorUserId, String pinCode) implements Command {
}
