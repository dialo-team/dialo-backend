package com.fit.se.application.command.settings;

import com.fit.se.application.common.command.Command;

public record ChangeConversationAliasCommand(String conversationId, Long actorUserId, String alias) implements Command {
}
