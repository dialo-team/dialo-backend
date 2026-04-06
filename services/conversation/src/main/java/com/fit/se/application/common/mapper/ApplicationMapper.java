package com.yourcompany.conversationservice.application.common.mapper;

public interface ApplicationMapper<S, T> {
    T map(S source);
}
