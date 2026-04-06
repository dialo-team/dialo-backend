package com.yourcompany.conversationservice.application.command.bootstrap;

import com.yourcompany.conversationservice.application.common.command.Command;

public record BootstrapUserConversationsCommand(Long userId) implements Command {
}
