package com.fit.se.domain.conversation;

import java.time.LocalDateTime;

public class ConversationMuteSetting {
    private ConversationMuteMode mode;
    private boolean allowMentionNotification;
    private boolean allowAllNotification;
    private boolean allowReminderNotification;
    private LocalDateTime mutedUntil;

    public ConversationMuteSetting(
            ConversationMuteMode mode,
            boolean allowMentionNotification,
            boolean allowAllNotification,
            boolean allowReminderNotification,
            LocalDateTime mutedUntil
    ) {
        this.mode = mode;
        this.allowMentionNotification = allowMentionNotification;
        this.allowAllNotification = allowAllNotification;
        this.allowReminderNotification = allowReminderNotification;
        this.mutedUntil = mutedUntil;
    }

    public ConversationMuteMode getMode() {
        return mode;
    }

    public boolean isMuted() {
        return mode != ConversationMuteMode.NONE;
    }
}
