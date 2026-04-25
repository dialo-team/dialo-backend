package com.fit.se.domain.conversation;

import com.fit.se.domain.conversation.state.ActiveState;
import com.fit.se.domain.direct.DirectConversation;
import com.fit.se.domain.direct.policy.DirectConversationPolicy;
import com.fit.se.domain.group.GroupConversation;
import com.fit.se.domain.group.GroupPermissionLevel;
import com.fit.se.domain.group.GroupSettings;
import com.fit.se.domain.group.policy.GroupConversationPolicy;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class ConversationFactory {

    public DirectConversation createDirect(String user1Id, String user2Id) {
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("Cannot create direct conversation with yourself");
        }

        Set<String> members = new LinkedHashSet<>();
        members.add(user1Id);
        members.add(user2Id);

        return new DirectConversation(
                UUID.randomUUID().toString(),
                members,
                new DirectConversationPolicy(),
                new ActiveState()
        );
    }

    public GroupConversation createGroup(String groupName, String creatorId, List<String> memberIds) {
        Set<String> members = new LinkedHashSet<>(memberIds);
        members.add(creatorId);

        if (members.size() < 3) {
            throw new IllegalArgumentException("Group must have at least 3 members");
        }

        GroupSettings settings = new GroupSettings(
                GroupPermissionLevel.LEADER_AND_DEPUTY,
                GroupPermissionLevel.LEADER_AND_DEPUTY,
                GroupPermissionLevel.LEADER_AND_DEPUTY,
                GroupPermissionLevel.LEADER_AND_DEPUTY,
                GroupPermissionLevel.ALL_MEMBERS,
                true,
                true,
                true,
                true
        );

        return new GroupConversation(
                UUID.randomUUID().toString(),
                groupName,
                members,
                creatorId,
                new GroupConversationPolicy(),
                new ActiveState(),
                settings
        );
    }
}