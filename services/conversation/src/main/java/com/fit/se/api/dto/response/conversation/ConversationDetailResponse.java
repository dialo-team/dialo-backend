package com.fit.se.api.dto.response.conversation;

import com.fit.se.api.dto.response.label.AssignedLabelResponse;
import com.fit.se.api.dto.response.settings.MemberSettingsResponse;

public record ConversationDetailResponse(
        String conversationId,
        String type,
        String status,
        GroupInfoResponse groupInfo,
        PermissionPolicyResponse permissionPolicy,
        Boolean approvalRequired,
        String joinToken,
        MemberSettingsResponse settings,
        AssignedLabelResponse assignedLabel
) {
}
