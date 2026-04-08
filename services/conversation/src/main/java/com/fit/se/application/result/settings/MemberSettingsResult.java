package com.fit.se.application.result.settings;

public record MemberSettingsResult(Boolean pinned, Boolean muted, Boolean hidden, String alias) {}
