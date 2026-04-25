package com.fit.se.domain.group;

public class PendingMemberRequest {
    private final String userId;
    private final String answer;

    public PendingMemberRequest(String userId, String answer) {
        this.userId = userId;
        this.answer = answer;
    }

    public String getUserId() {
        return userId;
    }

    public String getAnswer() {
        return answer;
    }
}
