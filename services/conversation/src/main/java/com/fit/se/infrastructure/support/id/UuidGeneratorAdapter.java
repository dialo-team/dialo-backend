package com.fit.se.infrastructure.support.id;

import com.fit.se.application.common.port.IdGeneratorPort;

import java.util.UUID;

public class UuidGeneratorAdapter implements IdGeneratorPort {
    @Override
    public UUID nextUuid() {
        return UUID.randomUUID();
    }
}
