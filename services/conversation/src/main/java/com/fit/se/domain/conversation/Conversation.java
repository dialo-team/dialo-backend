package com.fit.se.domain.conversation;

import com.fit.se.domain.conversation.state.ActiveState;
import com.fit.se.domain.conversation.state.ArchivedState;
import com.fit.se.domain.conversation.state.DissolvedState;
import com.fit.se.domain.person.Person;
import com.fit.se.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Conversation {
    protected final String id;
    protected final ConversationType type;
    protected final Set<String> memberIds;
    protected final ConversationPolicy policy;
    protected ConversationState state;
    protected ConversationMuteSetting muteSetting;

    protected final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Conversation(
            String id,
            ConversationType type,
            Set<String> memberIds,
            ConversationPolicy policy,
            ConversationState state
    ) {
        this.id = id;
        this.type = type;
        this.memberIds = new LinkedHashSet<>(memberIds);
        this.policy = policy;
        this.state = state;
    }

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> copied = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(copied);
    }

    public String getId() {
        return id;
    }

    public ConversationType getType() {
        return type;
    }

    public ConversationStatus getStatus() {
        return state.getStatus();
    }

    public boolean hasMember(String userId) {
        return memberIds.contains(userId);
    }

    protected void ensureStateAllows(Person actor) {
        state.validateCanOperate(this, actor);
    }

    public void archive() {
        this.state = new ArchivedState();
    }

    public void activate() {
        this.state = new ActiveState();
    }

    public void markDissolved() {
        this.state = new DissolvedState();
    }

    public void mute(ConversationMuteSetting muteSetting) {
        this.muteSetting = muteSetting;
    }

    public void unmute() {
        this.muteSetting = new ConversationMuteSetting(
                ConversationMuteMode.NONE,
                false,
                false,
                false,
                null
        );
    }

    public ConversationMuteSetting getMuteSetting() {
        return muteSetting;
    }
}
