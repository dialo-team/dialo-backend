package com.fit.se.domain.relationship;

import org.axonframework.messaging.eventhandling.annotation.Event;

@Event
public record SendFriendEvent(
    String source,
    String target
) {}
