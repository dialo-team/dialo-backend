package com.fit.se.infrastructure.persistence.entity.settings;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversation_member_settings")
public class ConversationMemberSettingsEntity {

    @EmbeddedId
    private ConversationMemberSettingsIdEmbeddable id;

    @Column(nullable = false)
    private boolean pinned;

    @Column(nullable = false)
    private boolean muted;

    @Column(nullable = false)
    private boolean hidden;

    @Column(name = "alias_name")
    private String alias;

    @Column(name = "assigned_label_id")
    private Long assignedLabelId;

    public ConversationMemberSettingsIdEmbeddable getId() { return id; }
    public void setId(ConversationMemberSettingsIdEmbeddable id) { this.id = id; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public Long getAssignedLabelId() { return assignedLabelId; }
    public void setAssignedLabelId(Long assignedLabelId) { this.assignedLabelId = assignedLabelId; }
}
