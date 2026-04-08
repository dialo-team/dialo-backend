package com.fit.se.application.command.bootstrap;

import com.fit.se.application.common.command.Command;

public record BootstrapUserConversationsCommand(Long userId) implements Command {
}
