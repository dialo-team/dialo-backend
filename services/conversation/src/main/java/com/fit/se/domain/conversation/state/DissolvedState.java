package com.fit.se.domain.conversation.state;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationState;
import com.fit.se.domain.conversation.ConversationStatus;
import com.fit.se.domain.exception.DomainException;
import com.fit.se.domain.person.Person;

public class DissolvedState implements ConversationState {
    @Override
    public void validateCanOperate(Conversation conversation, Person actor) {
        throw new DomainException("Cuộc trò chuyện đã bị giải tán");
    }

    @Override
    public ConversationStatus getStatus() {
        return ConversationStatus.DISSOLVED;
    }
}
