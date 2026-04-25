package com.fit.se.domain.direct.policy;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationPolicy;
import com.fit.se.domain.exception.DomainException;
import com.fit.se.domain.group.GroupConversation;
import com.fit.se.domain.group.GroupPermissionLevel;
import com.fit.se.domain.person.Person;

public class DirectConversationPolicy implements ConversationPolicy {

    @Override
    public void validateSendMessage(Conversation conversation, Person actor) {
        if (!conversation.hasMember(actor.getId())) {
            throw new DomainException("Người dùng không thuộc cuộc trò chuyện trực tiếp");
        }
    }

    @Override
    public void validateAddMember(Conversation conversation, Person actor, Person target) {
        throw new DomainException("Không thể thêm thành viên vào cuộc trò chuyện trực tiếp");
    }

    @Override
    public void validateRemoveMember(Conversation conversation, Person actor, Person target) {
        throw new DomainException("Không thể xóa thành viên khỏi cuộc trò chuyện trực tiếp");
    }

    @Override
    public void validateAssignDeputy(Conversation conversation, Person actor, Person target) {
        throw new DomainException("Cuộc trò chuyện trực tiếp không có vai trò phó nhóm");
    }

    @Override
    public void validateRevokeDeputy(Conversation conversation, Person actor, Person target) {
        throw new DomainException("Cuộc trò chuyện trực tiếp không có vai trò phó nhóm");
    }

    @Override
    public void validateTransferLeader(Conversation conversation, Person actor, Person target) {
        throw new DomainException("Cuộc trò chuyện trực tiếp không có vai trò trưởng nhóm");
    }

    @Override
    public void validateDissolve(Conversation conversation, Person actor) {
        throw new DomainException("Không thể giải tán cuộc trò chuyện trực tiếp theo chính sách vai trò");
    }
}
