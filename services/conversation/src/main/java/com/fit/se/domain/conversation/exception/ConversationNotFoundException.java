package com.fit.se.domain.conversation.exception;

import com.fit.se.domain.common.exception.ResourceNotFoundException;

public class ConversationNotFoundException extends ResourceNotFoundException {
    public ConversationNotFoundException(String message) {
        super(message);
    }

    public ConversationNotFoundException() {
        this("Conversation not found");
    }
}
