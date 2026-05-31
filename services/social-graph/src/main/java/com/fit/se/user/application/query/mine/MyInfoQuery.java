package com.fit.se.user.application.query.mine;

import lombok.Builder;

@Builder
public record MyInfoQuery(
    String current
) {}
