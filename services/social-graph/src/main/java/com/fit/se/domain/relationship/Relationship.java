package com.fit.se.domain.relationship;

import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@EventSourced
public class Relationship {
    private String id;

    private Map<String, RelationStatus> relations = new HashMap<>();

    private Instant createDate;
    private Instant modifiededDate;

    @EventSourcingHandler
    private void on(SendFriendEvent event) {
        relations.put(event.target(), RelationStatus.PENDING_OUTGOING);
    }

    private void on() {

    }

    @EntityCreator
    public Relationship() {}

}
