package com.fit.se.application.conversation;

import com.fit.se.domain.conversation.ConversationFactory;
import com.fit.se.domain.direct.DirectConversation;
import com.fit.se.domain.direct.DirectConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnsureDirectConversationUseCase {

    private final DirectConversationRepository directConversationRepository;
    private final ConversationFactory conversationFactory;

    @Transactional
    public DirectConversation execute(String user1Id, String user2Id) {
        return directConversationRepository
                .findByParticipants(user1Id, user2Id)
                .orElseGet(() -> {
                    DirectConversation conversation =
                            conversationFactory.createDirect(user1Id, user2Id);
                    return directConversationRepository.save(conversation);
                });
    }
}