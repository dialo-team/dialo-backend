package com.yourcompany.conversationservice.application.result.conversation;

import com.yourcompany.conversationservice.application.common.command.CommandResult;

public record ConversationCreatedResult(String conversationId, String type, String status) implements CommandResult {
}
