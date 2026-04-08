package com.fit.se.application.handler.command.join;

import com.fit.se.application.command.join.CancelJoinRequestCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.join.JoinRequestResult;

@CommandHandler
public class CancelJoinRequestHandler {
    public JoinRequestResult handle(CancelJoinRequestCommand command) {
        throw new UnsupportedOperationException("Implement use case: CancelJoinRequestHandler");
    }
}
