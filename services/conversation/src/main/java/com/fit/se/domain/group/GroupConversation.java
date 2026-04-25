package com.fit.se.domain.group;

import com.fit.se.domain.conversation.Conversation;
import com.fit.se.domain.conversation.ConversationPolicy;
import com.fit.se.domain.conversation.ConversationState;
import com.fit.se.domain.conversation.ConversationType;
import com.fit.se.domain.exception.DomainException;
import com.fit.se.domain.group.events.*;
import com.fit.se.domain.person.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GroupConversation extends Conversation {
    private String name;
    private String avatar;
    private String description;
    private String joinLink;
    private String approvalQuestion;

    private final Map<String, GroupRole> roles = new HashMap<>();
    private final GroupSettings settings;

    private final Map<String, PendingMemberRequest> pendingRequests = new HashMap<>();
    private final Set<String> blockedMemberIds = new HashSet<>();

    public GroupConversation(
            String id,
            String name,
            Set<String> memberIds,
            String leaderId,
            ConversationPolicy policy,
            ConversationState state,
            GroupSettings settings
    ) {
        super(id, ConversationType.GROUP, memberIds, policy, state);
        this.name = name;
        this.avatar = null;
        this.settings = settings;

        if (!memberIds.contains(leaderId)) {
            throw new DomainException("Trưởng nhóm phải là thành viên của nhóm");
        }

        for (String memberId : memberIds) {
            roles.put(memberId, GroupRole.MEMBER);
        }
        roles.put(leaderId, GroupRole.LEADER);
    }

    public void addMember(Person actor, Person target) {
        ensureStateAllows(actor);
        policy.validateAddMember(this, actor, target);

        memberIds.add(target.getId());
        roles.put(target.getId(), GroupRole.MEMBER);
        registerEvent(new MemberAddedEvent(getId(), target.getId()));
    }

    public void removeMember(Person actor, Person target) {
        ensureStateAllows(actor);
        policy.validateRemoveMember(this, actor, target);

        memberIds.remove(target.getId());
        roles.remove(target.getId());
        registerEvent(new MemberRemovedEvent(getId(), target.getId()));
    }

    public void assignDeputy(Person actor, Person target) {
        ensureStateAllows(actor);
        policy.validateAssignDeputy(this, actor, target);

        roles.put(target.getId(), GroupRole.DEPUTY);
        registerEvent(new RoleAssignedEvent(getId(), target.getId(), GroupRole.DEPUTY));
    }

    public void transferLeader(Person actor, Person target) {
        ensureStateAllows(actor);
        policy.validateTransferLeader(this, actor, target);

        String oldLeaderId = actor.getId();
        roles.put(actor.getId(), GroupRole.MEMBER);
        roles.put(target.getId(), GroupRole.LEADER);
        registerEvent(new LeaderTransferredEvent(getId(), oldLeaderId, target.getId()));
    }

    public void dissolve(Person actor) {
        ensureStateAllows(actor);
        policy.validateDissolve(this, actor);

        markDissolved();
        registerEvent(new GroupDissolvedEvent(getId()));
    }

    public void changeAvatar(Person actor, String avatar) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);
        this.avatar = avatar;
    }

    public void updateDescription(Person actor, String description) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);
        this.description = description;
    }

    public void changeJoinLink(Person actor, String newJoinLink) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);
        this.joinLink = newJoinLink;
    }

    public String getJoinLink() {
        return joinLink;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void blockMember(Person actor, Person target) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);

        if (!hasMember(target.getId())) {
            throw new DomainException("Người dùng mục tiêu không thuộc nhóm");
        }

        if (getRoleOf(target.getId()) == GroupRole.LEADER) {
            throw new DomainException("Không thể chặn trưởng nhóm");
        }

        blockedMemberIds.add(target.getId());
    }

    public void unblockMember(Person actor, Person target) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);

        if (!blockedMemberIds.contains(target.getId())) {
            throw new DomainException("Người dùng mục tiêu hiện không bị chặn");
        }

        blockedMemberIds.remove(target.getId());
    }

    public boolean isBlockedMember(String userId) {
        return blockedMemberIds.contains(userId);
    }

    public void joinByLink(Person actor, String link) {
        ensureStateAllows(actor);

        if (!settings.isJoinByLinkEnabled()) {
            throw new DomainException("Tính năng tham gia bằng liên kết đang bị tắt");
        }

        if (joinLink == null || !joinLink.equals(link)) {
            throw new DomainException("Liên kết tham gia không hợp lệ");
        }

        if (settings.isNewMemberApprovalEnabled()) {
            pendingRequests.put(actor.getId(), new PendingMemberRequest(actor.getId(), null));
            return;
        }

        memberIds.add(actor.getId());
        roles.put(actor.getId(), GroupRole.MEMBER);
    }

    public void updateApprovalQuestion(Person actor, String question) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);
        this.approvalQuestion = question;
    }

    public String getApprovalQuestion() {
        return approvalQuestion;
    }

    public void requestJoin(Person actor) {
        ensureStateAllows(actor);

        if (hasMember(actor.getId())) {
            throw new DomainException("Người dùng đã ở trong nhóm");
        }

        if (!settings.isNewMemberApprovalEnabled()) {
            memberIds.add(actor.getId());
            roles.put(actor.getId(), GroupRole.MEMBER);
            return;
        }

        pendingRequests.put(actor.getId(), new PendingMemberRequest(actor.getId(), null));
    }

    public void submitApprovalAnswer(Person actor, String answer) {
        ensureStateAllows(actor);

        if (hasMember(actor.getId())) {
            throw new DomainException("Người dùng đã ở trong nhóm");
        }

        if (!settings.isNewMemberApprovalEnabled()) {
            throw new DomainException("Chế độ phê duyệt đang bị tắt");
        }

        pendingRequests.put(actor.getId(), new PendingMemberRequest(actor.getId(), answer));
    }

    public void approvePendingMember(Person actor, Person target) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);

        PendingMemberRequest request = pendingRequests.get(target.getId());
        if (request == null) {
            throw new DomainException("Không tìm thấy yêu cầu chờ duyệt");
        }

        pendingRequests.remove(target.getId());
        memberIds.add(target.getId());
        roles.put(target.getId(), GroupRole.MEMBER);
    }

    public void rejectPendingMemberRequest(Person actor, Person target) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);

        if (!pendingRequests.containsKey(target.getId())) {
            throw new DomainException("Không tìm thấy yêu cầu chờ duyệt");
        }

        pendingRequests.remove(target.getId());
    }

    public String getName() {
        return name;
    }

    public GroupRole getRoleOf(String userId) {
        GroupRole role = roles.get(userId);
        if (role == null) {
            throw new DomainException("Người dùng không thuộc nhóm");
        }
        return role;
    }

    public void rename(Person actor, String newName) {
        ensureStateAllows(actor);
        ensureCanManageBasic(actor);
        this.name = newName;
    }

    public void validateSendMessage(Person actor) {
        ensureStateAllows(actor);
        policy.validateSendMessage(this, actor);
    }

    public void revokeDeputy(Person actor, Person target) {
        ensureStateAllows(actor);
        policy.validateRevokeDeputy(this, actor, target);

        roles.put(target.getId(), GroupRole.MEMBER);
    }

    public void leave(Person actor) {
        ensureStateAllows(actor);

        if (!hasMember(actor.getId())) {
            throw new DomainException("Người dùng không thuộc nhóm");
        }

        GroupRole role = getRoleOf(actor.getId());
        if (role == GroupRole.LEADER) {
            throw new DomainException("Trưởng nhóm không thể rời nhóm trực tiếp, hãy chuyển quyền trưởng nhóm hoặc giải tán nhóm trước");
        }

        memberIds.remove(actor.getId());
        roles.remove(actor.getId());
    }

    private void ensureCanManageBasic(Person actor) {
        GroupRole role = getRoleOf(actor.getId());
        if (role == GroupRole.MEMBER) {
            throw new DomainException("Thành viên thường không thể quản lý nhóm");
        }
    }

    public GroupSettings getSettings() {
        return settings;
    }


}
