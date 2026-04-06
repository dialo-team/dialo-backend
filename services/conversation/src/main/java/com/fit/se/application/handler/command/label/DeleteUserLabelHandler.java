package com.yourcompany.conversationservice.application.handler.command.label;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.label.DeleteUserLabelCommand;
import com.yourcompany.conversationservice.application.result.label.UserLabelResult;

@CommandHandler
public class DeleteUserLabelHandler {
    public UserLabelResult handle(DeleteUserLabelCommand command) {
        throw new UnsupportedOperationException("Implement use case: DeleteUserLabelHandler");
    }
}
