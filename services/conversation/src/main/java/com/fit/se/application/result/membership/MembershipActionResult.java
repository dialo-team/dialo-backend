package com.fit.se.application.result.membership;

public record MembershipActionResult(String conversationId, Long userId, String action, String status) {}
