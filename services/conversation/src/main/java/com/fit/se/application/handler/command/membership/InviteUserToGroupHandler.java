package com.fit.se.application.handler.command.membership;

import com.fit.se.application.command.membership.InviteUserToGroupCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.membership.MembershipActionResult;

@CommandHandler
public class InviteUserToGroupHandler {
    public MembershipActionResult handle(InviteUserToGroupCommand command) {
        throw new UnsupportedOperationException("Implement use case: InviteUserToGroupHandler");
    }
}
