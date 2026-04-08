package com.fit.se.application.result.join;

public record JoinRequestResult(String joinRequestId, String conversationId, Long requesterId, String joinMethod, String status) {}
