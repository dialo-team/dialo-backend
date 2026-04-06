package com.yourcompany.conversationservice.application.command.join;

import com.yourcompany.conversationservice.application.common.command.Command;

public record JoinGroupByTokenCommand(Long actorUserId, String joinToken, String joinMethod) implements Command {
}
