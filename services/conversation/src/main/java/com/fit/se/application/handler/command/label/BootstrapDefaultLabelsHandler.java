package com.yourcompany.conversationservice.application.handler.command.label;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.label.BootstrapDefaultLabelsCommand;
import com.yourcompany.conversationservice.application.result.label.UserLabelResult;

@CommandHandler
public class BootstrapDefaultLabelsHandler {
    public UserLabelResult handle(BootstrapDefaultLabelsCommand command) {
        throw new UnsupportedOperationException("Implement use case: BootstrapDefaultLabelsHandler");
    }
}
