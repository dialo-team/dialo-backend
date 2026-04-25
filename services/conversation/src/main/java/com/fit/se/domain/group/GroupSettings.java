package com.fit.se.domain.group;

public class GroupSettings {
    private GroupPermissionLevel editGroupInfoPermission;
    private GroupPermissionLevel pinMessagePermission;
    private GroupPermissionLevel createNoteAndReminderPermission;
    private GroupPermissionLevel createPollPermission;
    private GroupPermissionLevel sendMessagePermission;

    private boolean newMemberApprovalEnabled;
    private boolean highlightLeaderMessagesEnabled;
    private boolean newMembersCanReadRecentMessages;
    private boolean joinByLinkEnabled;

    public GroupSettings(
            GroupPermissionLevel editGroupInfoPermission,
            GroupPermissionLevel pinMessagePermission,
            GroupPermissionLevel createNoteAndReminderPermission,
            GroupPermissionLevel createPollPermission,
            GroupPermissionLevel sendMessagePermission,
            boolean newMemberApprovalEnabled,
            boolean highlightLeaderMessagesEnabled,
            boolean newMembersCanReadRecentMessages,
            boolean joinByLinkEnabled
    ) {
        this.editGroupInfoPermission = editGroupInfoPermission;
        this.pinMessagePermission = pinMessagePermission;
        this.createNoteAndReminderPermission = createNoteAndReminderPermission;
        this.createPollPermission = createPollPermission;
        this.sendMessagePermission = sendMessagePermission;
        this.newMemberApprovalEnabled = newMemberApprovalEnabled;
        this.highlightLeaderMessagesEnabled = highlightLeaderMessagesEnabled;
        this.newMembersCanReadRecentMessages = newMembersCanReadRecentMessages;
        this.joinByLinkEnabled = joinByLinkEnabled;
    }

    public boolean isNewMemberApprovalEnabled() {
        return newMemberApprovalEnabled;
    }

    public boolean isHighlightLeaderMessagesEnabled() {
        return highlightLeaderMessagesEnabled;
    }

    public boolean isNewMembersCanReadRecentMessages() {
        return newMembersCanReadRecentMessages;
    }

    public boolean isJoinByLinkEnabled() {
        return joinByLinkEnabled;
    }

    public void setNewMemberApprovalEnabled(boolean value) {
        this.newMemberApprovalEnabled = value;
    }

    public void setHighlightLeaderMessagesEnabled(boolean value) {
        this.highlightLeaderMessagesEnabled = value;
    }

    public void setNewMembersCanReadRecentMessages(boolean value) {
        this.newMembersCanReadRecentMessages = value;
    }

    public void setJoinByLinkEnabled(boolean value) {
        this.joinByLinkEnabled = value;
    }

    public GroupPermissionLevel getEditGroupInfoPermission() {
        return editGroupInfoPermission;
    }

    public GroupPermissionLevel getPinMessagePermission() {
        return pinMessagePermission;
    }

    public GroupPermissionLevel getCreateNoteAndReminderPermission() {
        return createNoteAndReminderPermission;
    }

    public GroupPermissionLevel getCreatePollPermission() {
        return createPollPermission;
    }

    public GroupPermissionLevel getSendMessagePermission() {
        return sendMessagePermission;
    }

    public void setEditGroupInfoPermission(GroupPermissionLevel value) {
        this.editGroupInfoPermission = value;
    }

    public void setPinMessagePermission(GroupPermissionLevel value) {
        this.pinMessagePermission = value;
    }

    public void setCreateNoteAndReminderPermission(GroupPermissionLevel value) {
        this.createNoteAndReminderPermission = value;
    }

    public void setCreatePollPermission(GroupPermissionLevel value) {
        this.createPollPermission = value;
    }

    public void setSendMessagePermission(GroupPermissionLevel value) {
        this.sendMessagePermission = value;
    }
}