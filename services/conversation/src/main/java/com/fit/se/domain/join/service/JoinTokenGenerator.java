package com.fit.se.domain.join.service;

import com.fit.se.domain.conversation.valueobject.JoinToken;

public interface JoinTokenGenerator {
    JoinToken generate();
}
