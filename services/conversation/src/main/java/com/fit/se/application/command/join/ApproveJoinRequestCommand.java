package com.yourcompany.conversationservice.application.command.join;

import com.yourcompany.conversationservice.application.common.command.Command;

public record ApproveJoinRequestCommand(String joinRequestId, Long actorUserId) implements Command {
}
