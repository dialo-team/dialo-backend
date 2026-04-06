package com.yourcompany.conversationservice.application.handler.command.label;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.label.CreateUserLabelCommand;
import com.yourcompany.conversationservice.application.result.label.UserLabelResult;

@CommandHandler
public class CreateUserLabelHandler {
    public UserLabelResult handle(CreateUserLabelCommand command) {
        throw new UnsupportedOperationException("Implement use case: CreateUserLabelHandler");
    }
}
