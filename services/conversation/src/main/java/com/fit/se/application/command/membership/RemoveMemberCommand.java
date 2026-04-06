package com.yourcompany.conversationservice.application.command.membership;

import com.yourcompany.conversationservice.application.common.command.Command;

public record RemoveMemberCommand(String conversationId, Long actorUserId, Long memberUserId) implements Command {
}
