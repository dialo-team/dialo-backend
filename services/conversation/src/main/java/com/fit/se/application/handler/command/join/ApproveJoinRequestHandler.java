package com.yourcompany.conversationservice.application.handler.command.join;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.join.ApproveJoinRequestCommand;
import com.yourcompany.conversationservice.application.result.join.JoinRequestResult;

@CommandHandler
public class ApproveJoinRequestHandler {
    public JoinRequestResult handle(ApproveJoinRequestCommand command) {
        throw new UnsupportedOperationException("Implement use case: ApproveJoinRequestHandler");
    }
}
