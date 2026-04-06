package com.yourcompany.conversationservice.application.handler.command.membership;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.membership.RemoveMemberCommand;
import com.yourcompany.conversationservice.application.result.membership.MembershipActionResult;

@CommandHandler
public class RemoveMemberHandler {
    public MembershipActionResult handle(RemoveMemberCommand command) {
        throw new UnsupportedOperationException("Implement use case: RemoveMemberHandler");
    }
}
