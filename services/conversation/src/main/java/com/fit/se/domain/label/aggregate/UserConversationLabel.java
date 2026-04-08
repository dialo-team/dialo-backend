package com.fit.se.domain.label.aggregate;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.label.exception.LabelDeletionNotAllowedException;
import com.fit.se.domain.label.exception.LabelRenameNotAllowedException;
import com.fit.se.domain.label.valueobject.LabelId;
import com.fit.se.domain.label.valueobject.LabelName;

import java.util.Objects;

public class UserConversationLabel {
    private final LabelId id;
    private final UserId ownerId;
    private LabelName name;
    private LabelColor color;
    private final LabelType type;
    private final boolean deletable;

    private UserConversationLabel(LabelId id, UserId ownerId, LabelName name, LabelColor color, LabelType type, boolean deletable) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.color = Objects.requireNonNull(color, "color must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.deletable = deletable;
    }

    public static UserConversationLabel defaultLabel(UserId ownerId, LabelName name, LabelColor color, boolean deletable) {
        return new UserConversationLabel(LabelId.newId(), ownerId, name, color, LabelType.DEFAULT, deletable);
    }

    public static UserConversationLabel customLabel(UserId ownerId, LabelName name, LabelColor color) {
        return new UserConversationLabel(LabelId.newId(), ownerId, name, color, LabelType.CUSTOM, true);
    }

    public LabelId getId() {
        return id;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public LabelName getName() {
        return name;
    }

    public LabelColor getColor() {
        return color;
    }

    public LabelType getType() {
        return type;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public boolean isProtectedDefault() {
        return type == LabelType.DEFAULT && !deletable;
    }

    public void rename(LabelName newName) {
        if (isProtectedDefault()) {
            throw new LabelRenameNotAllowedException("Protected default label cannot be renamed");
        }
        this.name = Objects.requireNonNull(newName, "newName must not be null");
    }

    public void recolor(LabelColor newColor) {
        this.color = Objects.requireNonNull(newColor, "newColor must not be null");
    }

    public void ensureDeletable() {
        if (!deletable) {
            throw new LabelDeletionNotAllowedException("Label cannot be deleted");
        }
    }
}
