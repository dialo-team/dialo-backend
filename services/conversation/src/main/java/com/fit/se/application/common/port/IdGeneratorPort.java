package com.fit.se.application.common.port;

import java.util.UUID;

public interface IdGeneratorPort {
    UUID nextUuid();
}
