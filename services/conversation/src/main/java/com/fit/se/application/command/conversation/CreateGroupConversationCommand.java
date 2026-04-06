package com.yourcompany.conversationservice.application.command.conversation;

import com.yourcompany.conversationservice.application.common.command.Command;

public record CreateGroupConversationCommand(Long ownerUserId, String groupName, String avatarUrl) implements Command {
}
