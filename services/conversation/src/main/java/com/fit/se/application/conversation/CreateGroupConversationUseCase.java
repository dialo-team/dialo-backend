package com.fit.se.application.conversation;

import com.fit.se.domain.conversation.ConversationFactory;
import com.fit.se.domain.conversation.ConversationRepository;
import com.fit.se.domain.group.GroupConversation;

import java.util.LinkedHashSet;
import java.util.Set;

public class CreateGroupConversationUseCase {
    private final ConversationRepository conversationRepository;
    private final ConversationFactory conversationFactory;

    public CreateGroupConversationUseCase(
            ConversationRepository conversationRepository,
            ConversationFactory conversationFactory
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationFactory = conversationFactory;
    }

    public GroupConversation execute(CreateGroupConversationCommand command) {
        Set<String> uniqueMembers = new LinkedHashSet<>(command.memberIds());
        uniqueMembers.add(command.creatorId());

        if (uniqueMembers.size() < 3) {
            throw new IllegalArgumentException("Group must have at least 3 members");
        }

        GroupConversation group = conversationFactory.createGroup(
                command.groupName(),
                command.creatorId(),
                uniqueMembers.stream().toList()
        );

        conversationRepository.save(group);
        return group;
    }
}