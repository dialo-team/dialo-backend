package com.yourcompany.conversationservice.application.result.membership;

import com.yourcompany.conversationservice.application.common.command.CommandResult;

public record MembershipActionResult(String conversationId, Long userId, String action, String status) implements CommandResult {
}
