package com.fit.se.application.handler.command.membership;

import com.fit.se.application.command.membership.RemoveMemberCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.membership.MembershipActionResult;

@CommandHandler
public class RemoveMemberHandler {
    public MembershipActionResult handle(RemoveMemberCommand command) {
        throw new UnsupportedOperationException("Implement use case: RemoveMemberHandler");
    }
}
