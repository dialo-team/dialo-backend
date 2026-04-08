package com.fit.se.domain.settings.aggregate;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.aggregate.ConversationType;
import com.fit.se.domain.conversation.aggregate.ConversationVisibilityPolicy;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.label.valueobject.LabelId;
import com.fit.se.domain.settings.exception.*;

import java.util.Objects;
import java.util.Optional;

public class ConversationMemberSettings {
    private final ConversationId conversationId;
    private final UserId userId;
    private PinState pinState;
    private MuteState muteState;
    private HideState hideState;
    private HiddenByPin hiddenByPin;
    private ConversationAlias alias;
    private LabelId assignedLabelId;

    public ConversationMemberSettings(ConversationId conversationId, UserId userId) {
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.pinState = PinState.DISABLED;
        this.muteState = MuteState.DISABLED;
        this.hideState = HideState.DISABLED;
        this.hiddenByPin = HiddenByPin.disabled();
    }

    public ConversationId getConversationId() {
        return conversationId;
    }

    public UserId getUserId() {
        return userId;
    }

    public PinState getPinState() {
        return pinState;
    }

    public MuteState getMuteState() {
        return muteState;
    }

    public HideState getHideState() {
        return hideState;
    }

    public HiddenByPin getHiddenByPin() {
        return hiddenByPin;
    }

    public Optional<ConversationAlias> getAlias() {
        return Optional.ofNullable(alias);
    }

    public Optional<LabelId> getAssignedLabelId() {
        return Optional.ofNullable(assignedLabelId);
    }

    public void pin() {
        if (hideState == HideState.ENABLED) {
            throw new CannotPinHiddenConversationException("Cannot pin hidden conversation");
        }
        this.pinState = PinState.ENABLED;
    }

    public void unpin() {
        this.pinState = PinState.DISABLED;
    }

    public void mute(ConversationType type) {
        ensureMuteSupported(type);
        this.muteState = MuteState.ENABLED;
    }

    public void unmute(ConversationType type) {
        ensureMuteSupported(type);
        this.muteState = MuteState.DISABLED;
    }

    public void hide(ConversationType type) {
        ensureHideSupported(type);
        this.hideState = HideState.ENABLED;
        this.hiddenByPin = HiddenByPin.enabled();
        this.pinState = PinState.DISABLED;
    }

    public void unhide(ConversationType type) {
        ensureHideSupported(type);
        this.hideState = HideState.DISABLED;
        this.hiddenByPin = HiddenByPin.disabled();
    }

    public void changeAlias(ConversationType type, ConversationAlias alias) {
        if (!ConversationVisibilityPolicy.supportsAlias(type)) {
            throw new AliasNotAllowedException("Alias is allowed only for direct and group conversations");
        }
        this.alias = Objects.requireNonNull(alias, "alias must not be null");
    }

    public void clearAlias(ConversationType type) {
        if (!ConversationVisibilityPolicy.supportsAlias(type)) {
            throw new AliasNotAllowedException("Alias is allowed only for direct and group conversations");
        }
        this.alias = null;
    }

    public void assignLabel(ConversationType type, LabelId labelId) {
        ensureLabelAssignmentSupported(type);
        this.assignedLabelId = Objects.requireNonNull(labelId, "labelId must not be null");
    }

    public void clearLabel(ConversationType type) {
        ensureLabelAssignmentSupported(type);
        this.assignedLabelId = null;
    }

    private void ensureMuteSupported(ConversationType type) {
        if (!ConversationVisibilityPolicy.supportsMute(type)) {
            throw new MuteNotAllowedException("Mute is allowed only for direct and group conversations");
        }
    }

    private void ensureHideSupported(ConversationType type) {
        if (!ConversationVisibilityPolicy.supportsHide(type)) {
            throw new HideNotAllowedException("Hide is allowed only for direct and group conversations");
        }
    }

    private void ensureLabelAssignmentSupported(ConversationType type) {
        if (!ConversationVisibilityPolicy.supportsAlias(type)) {
            throw new LabelAssignmentNotAllowedException("Label assignment is allowed only for direct and group conversations");
        }
    }
}
