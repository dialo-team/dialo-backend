package com.fit.se.domain.conversation;

import com.fit.se.domain.group.GroupConversation;
import com.fit.se.domain.group.GroupPermissionLevel;
import com.fit.se.domain.person.Person;

public interface ConversationPolicy {
    void validateSendMessage(Conversation conversation, Person actor);
    void validateAddMember(Conversation conversation, Person actor, Person target);
    void validateRemoveMember(Conversation conversation, Person actor, Person target);
    void validateAssignDeputy(Conversation conversation, Person actor, Person target);
    void validateRevokeDeputy(Conversation conversation, Person actor, Person target);
    void validateTransferLeader(Conversation conversation, Person actor, Person target);
    void validateDissolve(Conversation conversation, Person actor);
}
