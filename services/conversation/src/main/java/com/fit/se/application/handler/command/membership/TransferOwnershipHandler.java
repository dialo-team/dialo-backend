package com.yourcompany.conversationservice.application.handler.command.membership;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.membership.TransferOwnershipCommand;
import com.yourcompany.conversationservice.application.result.membership.MembershipActionResult;

@CommandHandler
public class TransferOwnershipHandler {
    public MembershipActionResult handle(TransferOwnershipCommand command) {
        throw new UnsupportedOperationException("Implement use case: TransferOwnershipHandler");
    }
}
