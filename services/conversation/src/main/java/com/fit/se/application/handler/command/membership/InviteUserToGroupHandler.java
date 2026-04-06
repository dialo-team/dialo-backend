package com.yourcompany.conversationservice.application.handler.command.membership;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.membership.InviteUserToGroupCommand;
import com.yourcompany.conversationservice.application.result.membership.MembershipActionResult;

@CommandHandler
public class InviteUserToGroupHandler {
    public MembershipActionResult handle(InviteUserToGroupCommand command) {
        throw new UnsupportedOperationException("Implement use case: InviteUserToGroupHandler");
    }
}
