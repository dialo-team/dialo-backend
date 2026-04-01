package com.fit.se.application.user.query.lookup;

import lombok.Builder;

@Builder
public record LookupQueryResult(
        String userId
) {}
