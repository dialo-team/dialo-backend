package com.fit.se.domain.direct;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationPolicy;
import com.fit.se.domain.conversation.ConversationState;
import com.fit.se.domain.conversation.ConversationType;
import com.fit.se.domain.exception.DomainException;
import com.fit.se.domain.person.Person;

import java.util.HashSet;
import java.util.Set;

public class DirectConversation extends Conversation {
    private final Set<String> blockedMessageSenderIds = new HashSet<>();

    public DirectConversation(
            String id,
            Set<String> memberIds,
            ConversationPolicy policy,
            ConversationState state
    ) {
        super(id, ConversationType.DIRECT, memberIds, policy, state);

        if (memberIds.size() != 2) {
            throw new DomainException("Cuộc trò chuyện trực tiếp phải có đúng 2 thành viên");
        }
    }

    public void blockMessagesFrom(Person actor, Person target) {
        ensureStateAllows(actor);

        if (!hasMember(actor.getId()) || !hasMember(target.getId())) {
            throw new DomainException("Cả hai người dùng phải thuộc cuộc trò chuyện trực tiếp");
        }

        blockedMessageSenderIds.add(target.getId());
    }

    public void unblockMessagesFrom(Person actor, Person target) {
        ensureStateAllows(actor);

        blockedMessageSenderIds.remove(target.getId());
    }

    public void validateSendMessage(Person actor) {
        ensureStateAllows(actor);
        policy.validateSendMessage(this, actor);

        if (blockedMessageSenderIds.contains(actor.getId())) {
            throw new DomainException("Người dùng hiện bị chặn gửi tin nhắn");
        }
    }
}
