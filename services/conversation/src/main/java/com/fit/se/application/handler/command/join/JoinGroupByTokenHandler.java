package com.fit.se.application.handler.command.join;

import com.fit.se.application.command.join.JoinGroupByTokenCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.membership.MembershipActionResult;

@CommandHandler
public class JoinGroupByTokenHandler {
    public MembershipActionResult handle(JoinGroupByTokenCommand command) {
        throw new UnsupportedOperationException("Implement use case: JoinGroupByTokenHandler");
    }
}
