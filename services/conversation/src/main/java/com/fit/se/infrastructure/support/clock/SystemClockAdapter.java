package com.fit.se.infrastructure.support.clock;

import com.fit.se.application.common.port.ClockPort;

import java.time.Instant;

public class SystemClockAdapter implements ClockPort {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
