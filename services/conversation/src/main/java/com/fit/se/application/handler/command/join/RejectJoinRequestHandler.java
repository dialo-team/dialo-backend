package com.yourcompany.conversationservice.application.handler.command.join;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.join.RejectJoinRequestCommand;
import com.yourcompany.conversationservice.application.result.join.JoinRequestResult;

@CommandHandler
public class RejectJoinRequestHandler {
    public JoinRequestResult handle(RejectJoinRequestCommand command) {
        throw new UnsupportedOperationException("Implement use case: RejectJoinRequestHandler");
    }
}
