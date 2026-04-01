package com.fit.se.application.user.query.mine;

import lombok.Builder;

@Builder
public record MyInfoQuery(
    String current
) {}
