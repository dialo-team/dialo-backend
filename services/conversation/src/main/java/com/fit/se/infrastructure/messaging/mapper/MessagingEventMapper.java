package com.fit.se.infrastructure.messaging.mapper;

import com.fit.se.application.command.bootstrap.BootstrapUserWorkspaceCommand;
import com.fit.se.infrastructure.messaging.dto.UserCreatedEventMessage;

public class MessagingEventMapper {

    public BootstrapUserWorkspaceCommand toBootstrapCommand(UserCreatedEventMessage message) {
        return new BootstrapUserWorkspaceCommand(message.userId(), message.phone());
    }
}
