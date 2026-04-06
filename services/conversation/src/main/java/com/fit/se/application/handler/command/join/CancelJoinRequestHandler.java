package com.yourcompany.conversationservice.application.handler.command.join;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.join.CancelJoinRequestCommand;
import com.yourcompany.conversationservice.application.result.join.JoinRequestResult;

@CommandHandler
public class CancelJoinRequestHandler {
    public JoinRequestResult handle(CancelJoinRequestCommand command) {
        throw new UnsupportedOperationException("Implement use case: CancelJoinRequestHandler");
    }
}
