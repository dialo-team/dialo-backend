package com.fit.se.application.result.bootstrap;

public record BootstrapStatusResult(Long userId, boolean selfConversationCreated, boolean systemConversationCreated, boolean defaultLabelsCreated) {}
