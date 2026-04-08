package com.fit.se.application.handler.command.membership;

import com.fit.se.application.command.membership.LeaveGroupCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.membership.MembershipActionResult;

@CommandHandler
public class LeaveGroupHandler {
    public MembershipActionResult handle(LeaveGroupCommand command) {
        throw new UnsupportedOperationException("Implement use case: LeaveGroupHandler");
    }
}
