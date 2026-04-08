package com.fit.se.application.port.input.conversation;

import com.fit.se.application.result.conversation.ConversationDetailResult;
import com.fit.se.application.result.conversation.ConversationSummaryResult;
import com.fit.se.application.result.settings.ConversationSettingsResult;

import java.util.List;

public interface ConversationQueryUseCase {
    List<ConversationSummaryResult> listUserConversations(Long currentUserId);
    ConversationDetailResult getConversationDetail(Long currentUserId, String conversationId);
    ConversationDetailResult getDirectConversation(Long currentUserId, Long otherUserId);
    ConversationSettingsResult getConversationSettings(Long currentUserId, String conversationId);
}
