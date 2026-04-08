package com.fit.se.application.result.conversation;

import com.fit.se.application.result.label.AssignedLabelResult;
import com.fit.se.application.result.settings.MemberSettingsResult;

public record ConversationDetailResult(
        String conversationId,
        String type,
        String status,
        GroupInfoResult groupInfo,
        PermissionPolicyResult permissionPolicy,
        Boolean approvalRequired,
        String joinToken,
        MemberSettingsResult settings,
        AssignedLabelResult assignedLabel
) {
}
