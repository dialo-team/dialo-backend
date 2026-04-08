package com.fit.se.application.command.settings;

import com.fit.se.application.common.command.Command;

public record UnhideConversationCommand(String conversationId, Long actorUserId, String pinCode) implements Command {
}
