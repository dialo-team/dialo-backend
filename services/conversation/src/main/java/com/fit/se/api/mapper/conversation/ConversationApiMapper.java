package com.fit.se.api.mapper.conversation;

import com.fit.se.api.dto.response.conversation.*;
import com.fit.se.api.mapper.label.LabelApiMapper;
import com.fit.se.api.mapper.settings.SettingsApiMapper;
import com.fit.se.application.result.conversation.ConversationCreatedResult;
import com.fit.se.application.result.conversation.ConversationDetailResult;
import com.fit.se.application.result.conversation.ConversationSummaryResult;
import org.springframework.stereotype.Component;

@Component
public class ConversationApiMapper {

    private final SettingsApiMapper settingsApiMapper;
    private final LabelApiMapper labelApiMapper;

    public ConversationApiMapper(SettingsApiMapper settingsApiMapper, LabelApiMapper labelApiMapper) {
        this.settingsApiMapper = settingsApiMapper;
        this.labelApiMapper = labelApiMapper;
    }

    public ConversationCreatedResponse toCreatedResponse(ConversationCreatedResult result) {
        return new ConversationCreatedResponse(result.conversationId(), result.type(), result.status());
    }

    public ConversationSummaryResponse toSummaryResponse(ConversationSummaryResult result) {
        return new ConversationSummaryResponse(result.conversationId(), result.type(), result.status(), result.title(), result.avatarUrl(), result.pinned(), result.muted(), result.hidden());
    }

    public ConversationDetailResponse toDetailResponse(ConversationDetailResult result) {
        return new ConversationDetailResponse(
                result.conversationId(),
                result.type(),
                result.status(),
                result.groupInfo() == null ? null : new GroupInfoResponse(result.groupInfo().name(), result.groupInfo().avatarUrl()),
                result.permissionPolicy() == null ? null : new PermissionPolicyResponse(result.permissionPolicy().editInfoScope(), result.permissionPolicy().sendMessageScope(), result.permissionPolicy().inviteScope()),
                result.approvalRequired(),
                result.joinToken(),
                result.settings() == null ? null : settingsApiMapper.toMemberSettingsResponse(result.settings()),
                result.assignedLabel() == null ? null : labelApiMapper.toAssignedLabelResponse(result.assignedLabel())
        );
    }
}
