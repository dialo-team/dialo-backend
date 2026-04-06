package com.yourcompany.conversationservice.application.command.settings;

import com.yourcompany.conversationservice.application.common.command.Command;

public record ChangeConversationAliasCommand(String conversationId, Long actorUserId, String alias) implements Command {
}
