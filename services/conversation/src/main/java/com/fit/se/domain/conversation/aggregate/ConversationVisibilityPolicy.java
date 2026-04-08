package com.fit.se.domain.conversation.aggregate;

public final class ConversationVisibilityPolicy {
    private ConversationVisibilityPolicy() {
    }

    public static boolean supportsMute(ConversationType type) {
        return type == ConversationType.DIRECT || type == ConversationType.GROUP;
    }

    public static boolean supportsHide(ConversationType type) {
        return type == ConversationType.DIRECT || type == ConversationType.GROUP;
    }

    public static boolean supportsAlias(ConversationType type) {
        return type == ConversationType.DIRECT || type == ConversationType.GROUP;
    }

    public static boolean supportsPin(ConversationType type) {
        return true;
    }
}

