package com.yourcompany.conversationservice.application.handler.command.label;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.label.ChangeUserLabelColorCommand;
import com.yourcompany.conversationservice.application.result.label.UserLabelResult;

@CommandHandler
public class ChangeUserLabelColorHandler {
    public UserLabelResult handle(ChangeUserLabelColorCommand command) {
        throw new UnsupportedOperationException("Implement use case: ChangeUserLabelColorHandler");
    }
}
