package com.fit.se.application.common.mapper;

public interface ApplicationMapper<S, T> {
    T map(S source);
}
