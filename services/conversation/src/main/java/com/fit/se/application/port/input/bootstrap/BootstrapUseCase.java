package com.fit.se.application.port.input.bootstrap;

import com.fit.se.api.dto.request.bootstrap.BootstrapUserRequest;
import com.fit.se.application.result.bootstrap.BootstrapStatusResult;

public interface BootstrapUseCase {
    BootstrapStatusResult bootstrapUser(BootstrapUserRequest request);
    BootstrapStatusResult getStatus(Long userId);
}
