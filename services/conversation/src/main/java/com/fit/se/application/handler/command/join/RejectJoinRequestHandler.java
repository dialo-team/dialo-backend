package com.fit.se.application.handler.command.join;

import com.fit.se.application.command.join.RejectJoinRequestCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.join.JoinRequestResult;

@CommandHandler
public class RejectJoinRequestHandler {
    public JoinRequestResult handle(RejectJoinRequestCommand command) {
        throw new UnsupportedOperationException("Implement use case: RejectJoinRequestHandler");
    }
}
