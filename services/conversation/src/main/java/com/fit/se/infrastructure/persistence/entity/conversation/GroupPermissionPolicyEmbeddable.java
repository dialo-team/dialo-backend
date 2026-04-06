package com.fit.se.infrastructure.persistence.entity.conversation;

import com.fit.se.domain.conversation.model.MemberPermissionScope;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class GroupPermissionPolicyEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "edit_info_scope", length = 20)
    private MemberPermissionScope editInfoScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_message_scope", length = 20)
    private MemberPermissionScope sendMessageScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_scope", length = 20)
    private MemberPermissionScope inviteScope;

    public MemberPermissionScope getEditInfoScope() { return editInfoScope; }
    public void setEditInfoScope(MemberPermissionScope editInfoScope) { this.editInfoScope = editInfoScope; }
    public MemberPermissionScope getSendMessageScope() { return sendMessageScope; }
    public void setSendMessageScope(MemberPermissionScope sendMessageScope) { this.sendMessageScope = sendMessageScope; }
    public MemberPermissionScope getInviteScope() { return inviteScope; }
    public void setInviteScope(MemberPermissionScope inviteScope) { this.inviteScope = inviteScope; }
}
