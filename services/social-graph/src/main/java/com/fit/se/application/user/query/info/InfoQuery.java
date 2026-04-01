package com.fit.se.application.user.query.info;

import lombok.Builder;

@Builder
public record InfoQuery(
    String target,
    String current,
    LookupType type

) {
    public enum LookupType {
        ID,
        PHONE,
        QR
    }
}
