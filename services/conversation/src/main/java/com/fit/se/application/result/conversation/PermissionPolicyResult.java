package com.yourcompany.conversationservice.application.result.conversation;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record PermissionPolicyResult(String editGroupInfoScope, String sendMessageScope, String inviteMemberScope) implements QueryResult {
}
