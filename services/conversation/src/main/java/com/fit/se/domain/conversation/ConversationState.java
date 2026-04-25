package com.fit.se.domain.conversation;

import com.fit.se.domain.person.Person;

public interface ConversationState {
    void validateCanOperate(Conversation conversation, Person actor);
    ConversationStatus getStatus();
}
