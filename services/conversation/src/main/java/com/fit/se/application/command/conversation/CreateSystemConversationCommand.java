package com.yourcompany.conversationservice.application.command.conversation;

import com.yourcompany.conversationservice.application.common.command.Command;

public record CreateSystemConversationCommand(Long userId) implements Command {
}
