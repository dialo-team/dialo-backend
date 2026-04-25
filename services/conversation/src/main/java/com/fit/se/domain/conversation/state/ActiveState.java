package com.fit.se.domain.conversation.state;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationState;
import com.fit.se.domain.conversation.ConversationStatus;
import com.fit.se.domain.person.Person;

public class ActiveState implements ConversationState {
    @Override
    public void validateCanOperate(Conversation conversation, Person actor) {
        // active thì cho phép xét tiếp theo policy
    }

    @Override
    public ConversationStatus getStatus() {
        return ConversationStatus.ACTIVE;
    }
}
