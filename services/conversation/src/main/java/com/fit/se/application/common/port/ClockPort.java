package com.fit.se.application.common.port;

import java.time.Instant;

public interface ClockPort {
    Instant now();
}
