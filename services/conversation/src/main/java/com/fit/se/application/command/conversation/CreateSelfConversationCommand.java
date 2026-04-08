package com.fit.se.application.command.conversation;

import com.fit.se.application.common.command.Command;

public record CreateSelfConversationCommand(Long userId) implements Command {
}
