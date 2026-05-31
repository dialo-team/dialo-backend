package com.fit.se.user.application.query.info;

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
