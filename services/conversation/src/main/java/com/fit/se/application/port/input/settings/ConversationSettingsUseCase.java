package com.fit.se.application.port.input.settings;

import com.fit.se.api.dto.request.settings.*;
import com.fit.se.application.result.settings.ConversationSettingsResult;

public interface ConversationSettingsUseCase {
    ConversationSettingsResult pin(Long currentUserId, String conversationId, PinConversationRequest request);
    ConversationSettingsResult unpin(Long currentUserId, String conversationId, UnpinConversationRequest request);
    ConversationSettingsResult mute(Long currentUserId, String conversationId, MuteConversationRequest request);
    ConversationSettingsResult unmute(Long currentUserId, String conversationId, UnmuteConversationRequest request);
    ConversationSettingsResult hide(Long currentUserId, String conversationId, HideConversationRequest request);
    ConversationSettingsResult unhide(Long currentUserId, String conversationId, UnhideConversationRequest request);
    ConversationSettingsResult changeAlias(Long currentUserId, String conversationId, ChangeConversationAliasRequest request);
    ConversationSettingsResult assignLabel(Long currentUserId, String conversationId, AssignConversationLabelRequest request);
    ConversationSettingsResult clearLabel(Long currentUserId, String conversationId, ClearConversationLabelRequest request);
}
