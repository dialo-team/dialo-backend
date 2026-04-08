package com.fit.se.application.command.conversation;

import com.fit.se.application.common.command.Command;

public record ToggleApprovalModeCommand(String conversationId, Long actorUserId, boolean approvalRequired) implements Command {
}
