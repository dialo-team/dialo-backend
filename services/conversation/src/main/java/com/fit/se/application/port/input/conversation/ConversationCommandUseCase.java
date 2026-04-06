package com.fit.se.application.port.input.conversation;

import com.fit.se.api.dto.request.conversation.*;
import com.fit.se.application.result.conversation.ConversationCreatedResult;
import com.fit.se.application.result.conversation.ConversationDetailResult;
import com.fit.se.application.result.join.JoinTokenResult;

public interface ConversationCommandUseCase {
    ConversationCreatedResult createDirect(Long currentUserId, CreateDirectConversationRequest request);
    ConversationCreatedResult createGroup(Long currentUserId, CreateGroupConversationRequest request);
    ConversationCreatedResult createSelf(CreateSelfConversationRequest request);
    ConversationCreatedResult createSystem(CreateSystemConversationRequest request);
    ConversationDetailResult updateGroupInfo(Long currentUserId, String conversationId, UpdateGroupInfoRequest request);
    ConversationDetailResult updatePermissionPolicy(Long currentUserId, String conversationId, UpdateGroupPermissionPolicyRequest request);
    ConversationDetailResult toggleApprovalMode(Long currentUserId, String conversationId, ToggleApprovalModeRequest request);
    JoinTokenResult rotateJoinToken(Long currentUserId, String conversationId, RotateJoinTokenRequest request);
    void dissolveGroup(Long currentUserId, String conversationId);
}
