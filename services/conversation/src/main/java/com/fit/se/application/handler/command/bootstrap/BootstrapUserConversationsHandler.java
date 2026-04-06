package com.yourcompany.conversationservice.application.handler.command.bootstrap;

import com.yourcompany.conversationservice.application.common.annotation.CommandHandler;
import com.yourcompany.conversationservice.application.command.bootstrap.BootstrapUserConversationsCommand;
import com.yourcompany.conversationservice.application.result.bootstrap.BootstrapStatusResult;

@CommandHandler
public class BootstrapUserConversationsHandler {
    public BootstrapStatusResult handle(BootstrapUserConversationsCommand command) {
        throw new UnsupportedOperationException("Implement use case: BootstrapUserConversationsHandler");
    }
}
