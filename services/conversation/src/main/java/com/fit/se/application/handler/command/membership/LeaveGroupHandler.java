package com.yourcompany.conversationservice.application.handler.command.membership;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.membership.LeaveGroupCommand;
import com.yourcompany.conversationservice.application.result.membership.MembershipActionResult;

@CommandHandler
public class LeaveGroupHandler {
    public MembershipActionResult handle(LeaveGroupCommand command) {
        throw new UnsupportedOperationException("Implement use case: LeaveGroupHandler");
    }
}
