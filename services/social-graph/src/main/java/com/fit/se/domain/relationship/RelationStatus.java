package com.fit.se.domain.relationship;

public enum RelationStatus {
    NONE,
    PENDING_OUTGOING,   // mình gửi request
    PENDING_INCOMING,   // người khác gửi cho mình
    FRIEND,
    BLOCKED
}
