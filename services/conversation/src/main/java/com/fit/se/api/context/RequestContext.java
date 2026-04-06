package com.fit.se.api.context;

import java.util.List;

public record RequestContext(
        Long userId,
        String username,
        List<String> roles,
        String traceId,
        String requestId
) {
}
