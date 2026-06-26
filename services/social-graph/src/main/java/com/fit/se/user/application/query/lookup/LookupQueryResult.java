package com.fit.se.user.application.query.lookup;

import lombok.Builder;

@Builder
public record LookupQueryResult(
        String userId
) {}
