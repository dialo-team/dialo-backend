package com.yourcompany.conversationservice.application.common.port;

import java.time.Instant;

public interface ClockPort {
    Instant now();
}
