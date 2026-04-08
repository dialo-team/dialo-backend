package com.fit.se.application.command.settings;

import com.fit.se.application.common.command.Command;

public record MuteConversationCommand(String conversationId, Long actorUserId) implements Command {
}
