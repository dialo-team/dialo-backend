package com.fit.se.application.handler.command.label;

import com.fit.se.application.command.label.DeleteUserLabelCommand;
import com.fit.se.application.common.annotation.CommandHandler;
import com.fit.se.application.result.label.UserLabelResult;

@CommandHandler
public class DeleteUserLabelHandler {
    public UserLabelResult handle(DeleteUserLabelCommand command) {
        throw new UnsupportedOperationException("Implement use case: DeleteUserLabelHandler");
    }
}
