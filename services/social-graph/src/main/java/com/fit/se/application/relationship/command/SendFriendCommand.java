package com.fit.se.application.relationship.command;

import org.axonframework.messaging.commandhandling.annotation.Command;

@Command
public record SendFriendCommand(
    String source,
    String target
) {}
