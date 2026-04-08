package com.fit.se.domain.conversation.aggregate;

import com.fit.se.domain.common.valueobject.ImageUrl;
import com.fit.se.domain.conversation.exception.ConversationDissolvedException;
import com.fit.se.domain.conversation.exception.UnsupportedConversationOperationException;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.conversation.valueobject.GroupName;
import com.fit.se.domain.conversation.valueobject.JoinToken;
import com.fit.se.domain.join.aggregate.ApprovalMode;

import java.util.Objects;
import java.util.Optional;

public class Conversation {
    private final ConversationId id;
    private final ConversationType type;
    private ConversationStatus status;
    private GroupInfo groupInfo;
    private GroupPermissionPolicy permissionPolicy;
    private ApprovalMode approvalMode;
    private JoinToken joinToken;

    private Conversation(
            ConversationId id,
            ConversationType type,
            ConversationStatus status,
            GroupInfo groupInfo,
            GroupPermissionPolicy permissionPolicy,
            ApprovalMode approvalMode,
            JoinToken joinToken
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.groupInfo = groupInfo;
        this.permissionPolicy = permissionPolicy;
        this.approvalMode = approvalMode;
        this.joinToken = joinToken;
    }

    public static Conversation newDirect(ConversationId id) {
        return new Conversation(id, ConversationType.DIRECT, ConversationStatus.ACTIVE, null, null, null, null);
    }

    public static Conversation newGroup(ConversationId id, GroupInfo groupInfo, GroupPermissionPolicy permissionPolicy) {
        return new Conversation(
                id,
                ConversationType.GROUP,
                ConversationStatus.ACTIVE,
                Objects.requireNonNull(groupInfo, "groupInfo must not be null"),
                Objects.requireNonNull(permissionPolicy, "permissionPolicy must not be null"),
                ApprovalMode.AUTO_APPROVE,
                JoinToken.randomToken()
        );
    }

    public static Conversation newSelf(ConversationId id) {
        GroupInfo info = new GroupInfo(new GroupName("My Documents"), null);
        return new Conversation(id, ConversationType.SELF, ConversationStatus.ACTIVE, info, null, null, null);
    }

    public static Conversation newSystem(ConversationId id) {
        return new Conversation(id, ConversationType.SYSTEM, ConversationStatus.ACTIVE, null, null, null, null);
    }

    public ConversationId getId() {
        return id;
    }

    public ConversationType getType() {
        return type;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public Optional<GroupInfo> getGroupInfo() {
        return Optional.ofNullable(groupInfo);
    }

    public Optional<GroupPermissionPolicy> getPermissionPolicy() {
        return Optional.ofNullable(permissionPolicy);
    }

    public Optional<ApprovalMode> getApprovalMode() {
        return Optional.ofNullable(approvalMode);
    }

    public Optional<JoinToken> getJoinToken() {
        return Optional.ofNullable(joinToken);
    }

    public boolean isGroup() {
        return type == ConversationType.GROUP;
    }

    public boolean isActive() {
        return status == ConversationStatus.ACTIVE;
    }

    public void dissolve() {
        ensureType(ConversationType.GROUP);
        this.status = ConversationStatus.DISSOLVED;
        this.joinToken = null;
    }

    public void updateGroupName(GroupName groupName) {
        ensureType(ConversationType.GROUP);
        ensureActive();
        this.groupInfo = this.groupInfo.withName(groupName);
    }

    public void updateGroupAvatar(ImageUrl imageUrl) {
        ensureType(ConversationType.GROUP);
        ensureActive();
        this.groupInfo = this.groupInfo.withAvatar(imageUrl);
    }

    public void updatePermissionPolicy(GroupPermissionPolicy newPolicy) {
        ensureType(ConversationType.GROUP);
        ensureActive();
        this.permissionPolicy = Objects.requireNonNull(newPolicy, "newPolicy must not be null");
    }

    public void changeApprovalMode(ApprovalMode newMode) {
        ensureType(ConversationType.GROUP);
        ensureActive();
        this.approvalMode = Objects.requireNonNull(newMode, "newMode must not be null");
    }

    public void rotateJoinToken(JoinToken newJoinToken) {
        ensureType(ConversationType.GROUP);
        ensureActive();
        this.joinToken = Objects.requireNonNull(newJoinToken, "newJoinToken must not be null");
    }

    public void clearJoinToken() {
        ensureType(ConversationType.GROUP);
        ensureActive();
        this.joinToken = null;
    }

    private void ensureType(ConversationType expectedType) {
        if (type != expectedType) {
            throw new UnsupportedConversationOperationException(
                    "Unsupported operation for conversation type: " + type
            );
        }
    }

    private void ensureActive() {
        if (!isActive()) {
            throw new ConversationDissolvedException("Conversation is dissolved");
        }
    }
}
