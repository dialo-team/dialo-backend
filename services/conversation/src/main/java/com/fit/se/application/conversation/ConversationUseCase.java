package com.fit.se.application.conversation;

import com.fit.se.domain.conversation.ConversationFactory;
import com.fit.se.domain.conversation.ConversationRepository;
import com.fit.se.domain.direct.DirectConversation;
import com.fit.se.domain.direct.DirectConversationRepository;
import com.fit.se.domain.exception.DomainException;
import com.fit.se.domain.group.GroupConversation;
import com.fit.se.domain.person.Person;
import com.fit.se.domain.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConversationUseCase {
    private final ConversationFactory conversationFactory;
    private final ConversationRepository conversationRepository;
    private final DirectConversationRepository directConversationRepository;
    private final PersonRepository personRepository;

    public DirectConversation createDirectConversation(String actorId, String targetId) {
        Person actor = getPersonOrThrow(actorId);
        Person target = getPersonOrThrow(targetId);

        directConversationRepository.findByParticipants(actor.getId(), target.getId())
                .ifPresent(existing -> {
                    throw new DomainException("Cuộc trò chuyện trực tiếp đã tồn tại");
                });

        DirectConversation conversation =
                conversationFactory.createDirect(actor.getId(), target.getId());

        conversationRepository.save(conversation);
        return conversation;
    }

    public GroupConversation createGroupConversation(
            String creatorId,
            String groupName,
            List<String> memberIds
    ) {
        Person creator = getPersonOrThrow(creatorId);

        validateGroupName(groupName);

        Set<String> uniqueMemberIds = new LinkedHashSet<>(memberIds);
        uniqueMemberIds.add(creator.getId());

        ensureAllMembersExist(uniqueMemberIds);

        GroupConversation conversation =
                conversationFactory.createGroup(groupName, creator.getId(), List.copyOf(uniqueMemberIds));

        conversationRepository.save(conversation);
        return conversation;
    }

    private Person getPersonOrThrow(String personId) {
        return (Person) personRepository.findById(personId).orElseThrow(() -> new DomainException("Không tìm thấy người dùng: " + personId));
    }

    private void ensureAllMembersExist(Set<String> memberIds) {
        for (String memberId : memberIds) {
            getPersonOrThrow(memberId);
        }
    }

    private void validateGroupName(String groupName) {
        if (groupName == null || groupName.isBlank()) {
            throw new DomainException("Tên nhóm không được để trống");
        }
    }
}
