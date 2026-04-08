package com.fit.se.application.handler.command.label;

import com.fit.se.application.command.label.CreateUserLabelCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.label.UserLabelResult;

@CommandHandler
public class CreateUserLabelHandler {
    public UserLabelResult handle(CreateUserLabelCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateUserLabelHandler");
    }
}
