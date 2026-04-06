package com.fit.se.application.command.bootstrap;

public record BootstrapUserWorkspaceCommand(
        String userId,
        String phone
) {
}
