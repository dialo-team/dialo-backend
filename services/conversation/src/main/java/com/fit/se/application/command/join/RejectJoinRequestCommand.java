package com.yourcompany.conversationservice.application.command.join;

import com.yourcompany.conversationservice.application.common.command.Command;

public record RejectJoinRequestCommand(String joinRequestId, Long actorUserId, String reason) implements Command {
}
