package com.fit.se.application.command.conversation;

import com.fit.se.application.common.command.Command;

public record CreateDirectConversationCommand(Long initiatorUserId, Long targetUserId) implements Command {
}
