package com.yourcompany.conversationservice.application.handler.command.label;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.label.RenameUserLabelCommand;
import com.yourcompany.conversationservice.application.result.label.UserLabelResult;

@CommandHandler
public class RenameUserLabelHandler {
    public UserLabelResult handle(RenameUserLabelCommand command) {
        throw new UnsupportedOperationException("Implement use case: RenameUserLabelHandler");
    }
}
