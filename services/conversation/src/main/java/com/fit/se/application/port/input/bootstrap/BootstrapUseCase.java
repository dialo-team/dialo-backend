package com.fit.se.application.port.input.bootstrap;

import com.fit.se.application.command.bootstrap.BootstrapUserWorkspaceCommand;

public interface BootstrapUseCase {
    void bootstrap(BootstrapUserWorkspaceCommand command);
}
