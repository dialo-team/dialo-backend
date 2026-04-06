package com.yourcompany.conversationservice.application.handler.command.join;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.join.JoinGroupByTokenCommand;
import com.yourcompany.conversationservice.application.result.membership.MembershipActionResult;

@CommandHandler
public class JoinGroupByTokenHandler {
    public MembershipActionResult handle(JoinGroupByTokenCommand command) {
        throw new UnsupportedOperationException("Implement use case: JoinGroupByTokenHandler");
    }
}
