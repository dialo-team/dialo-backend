package com.fit.se.domain.group.policy;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationPolicy;
import com.fit.se.domain.exception.DomainException;
import com.fit.se.domain.group.GroupConversation;
import com.fit.se.domain.group.GroupPermissionLevel;
import com.fit.se.domain.group.GroupRole;
import com.fit.se.domain.person.Person;

public class GroupConversationPolicy implements ConversationPolicy {

    @Override
    public void validateAddMember(Conversation conversation, Person actor, Person target) {
        GroupConversation group = asGroup(conversation);
        ensureMember(group, actor);

        GroupRole actorRole = group.getRoleOf(actor.getId());
        if (actorRole == GroupRole.MEMBER) {
            throw new DomainException("Thành viên thường không thể thêm thành viên mới");
        }

        if (group.hasMember(target.getId())) {
            throw new DomainException("Người dùng mục tiêu đã ở trong nhóm");
        }
    }

    @Override
    public void validateRemoveMember(Conversation conversation, Person actor, Person target) {
        GroupConversation group = asGroup(conversation);
        ensureMember(group, actor);
        ensureMember(group, target);

        GroupRole actorRole = group.getRoleOf(actor.getId());
        GroupRole targetRole = group.getRoleOf(target.getId());

        if (actorRole == GroupRole.MEMBER) {
            throw new DomainException("Thành viên thường không thể xóa thành viên");
        }

        if (actorRole == GroupRole.DEPUTY && targetRole != GroupRole.MEMBER) {
            throw new DomainException("Phó nhóm chỉ có thể xóa thành viên thường");
        }

        if (actor.getId().equals(target.getId())) {
            throw new DomainException("Hãy dùng leaveGroup thay vì tự xóa chính mình");
        }
    }

    @Override
    public void validateAssignDeputy(Conversation conversation, Person actor, Person target) {
        GroupConversation group = asGroup(conversation);
        ensureMember(group, actor);
        ensureMember(group, target);

        if (group.getRoleOf(actor.getId()) != GroupRole.LEADER) {
            throw new DomainException("Chỉ trưởng nhóm mới có thể bổ nhiệm phó nhóm");
        }

        if (group.getRoleOf(target.getId()) == GroupRole.LEADER) {
            throw new DomainException("Trưởng nhóm đã là vai trò cao nhất");
        }
    }

    @Override
    public void validateRevokeDeputy(Conversation conversation, Person actor, Person target) {
        GroupConversation group = asGroup(conversation);
        ensureMember(group, actor);
        ensureMember(group, target);

        if (group.getRoleOf(actor.getId()) != GroupRole.LEADER) {
            throw new DomainException("Chỉ trưởng nhóm mới có thể thu hồi quyền phó nhóm");
        }

        if (group.getRoleOf(target.getId()) != GroupRole.DEPUTY) {
            throw new DomainException("Người dùng mục tiêu không phải là phó nhóm");
        }
    }

    @Override
    public void validateTransferLeader(Conversation conversation, Person actor, Person target) {
        GroupConversation group = asGroup(conversation);
        ensureMember(group, actor);
        ensureMember(group, target);

        if (group.getRoleOf(actor.getId()) != GroupRole.LEADER) {
            throw new DomainException("Chỉ trưởng nhóm mới có thể chuyển quyền trưởng nhóm");
        }

        if (actor.getId().equals(target.getId())) {
            throw new DomainException("Người dùng mục tiêu đã là trưởng nhóm");
        }
    }

    @Override
    public void validateDissolve(Conversation conversation, Person actor) {
        GroupConversation group = asGroup(conversation);
        ensureMember(group, actor);

        if (group.getRoleOf(actor.getId()) != GroupRole.LEADER) {
            throw new DomainException("Chỉ trưởng nhóm mới có thể giải tán nhóm");
        }
    }

    private void validateByPermissionLevel(GroupConversation group, Person actor, GroupPermissionLevel level) {
        ensureMember(group, actor);

        GroupRole role = group.getRoleOf(actor.getId());

        switch (level) {
            case LEADER_AND_DEPUTY -> {
                if (role != GroupRole.LEADER && role != GroupRole.DEPUTY) {
                    throw new DomainException("Chỉ trưởng nhóm hoặc phó nhóm mới có thể thực hiện thao tác này");
                }
            }
            case ALL_MEMBERS -> {
                // chỉ cần là member trong nhóm
            }
        }
    }

    private GroupConversation asGroup(Conversation conversation) {
        if (!(conversation instanceof GroupConversation)) {
            throw new DomainException("Cuộc trò chuyện này không phải là nhóm");
        }
        return (GroupConversation) conversation;
    }

    private void ensureMember(GroupConversation group, Person user) {
        if (!group.hasMember(user.getId())) {
            throw new DomainException("Người dùng không thuộc nhóm");
        }
    }

    public void validateRenameConversation(Conversation conversation, Person actor) {
        GroupConversation group = asGroup(conversation);
        validateByPermissionLevel(
                group,
                actor,
                group.getSettings().getEditGroupInfoPermission()
        );
    }

    public void validatePinItem(Conversation conversation, Person actor) {
        GroupConversation group = asGroup(conversation);
        validateByPermissionLevel(
                group,
                actor,
                group.getSettings().getPinMessagePermission()
        );
    }

    public void validateCreateNoteOrReminder(Conversation conversation, Person actor) {
        GroupConversation group = asGroup(conversation);
        validateByPermissionLevel(
                group,
                actor,
                group.getSettings().getCreateNoteAndReminderPermission()
        );
    }

    public void validateCreatePoll(Conversation conversation, Person actor) {
        GroupConversation group = asGroup(conversation);
        validateByPermissionLevel(
                group,
                actor,
                group.getSettings().getCreatePollPermission()
        );
    }

    @Override
    public void validateSendMessage(Conversation conversation, Person actor) {
        GroupConversation group = asGroup(conversation);
        validateByPermissionLevel(group, actor, group.getSettings().getSendMessagePermission());

        if (group.isBlockedMember(actor.getId())) {
            throw new DomainException("Thành viên bị chặn không thể gửi tin nhắn");
        }
    }
}
