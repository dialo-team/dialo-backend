package com.fit.se.application.handler.command.membership;

import com.fit.se.application.command.membership.TransferOwnershipCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.membership.MembershipActionResult;

@CommandHandler
public class TransferOwnershipHandler {
    public MembershipActionResult handle(TransferOwnershipCommand command) {
        throw new UnsupportedOperationException("Implement use case: TransferOwnershipHandler");
    }
}
