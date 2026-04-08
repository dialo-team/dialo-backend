package com.fit.se.domain.conversation.aggregate;

import com.fit.se.domain.common.valueobject.ImageUrl;
import com.fit.se.domain.conversation.valueobject.GroupName;

import java.util.Objects;
import java.util.Optional;

public final class GroupInfo {
    private final GroupName name;
    private final ImageUrl avatarUrl;

    public GroupInfo(GroupName name, ImageUrl avatarUrl) {
        this.name = Objects.requireNonNull(name, "group name must not be null");
        this.avatarUrl = avatarUrl;
    }

    public GroupName getName() {
        return name;
    }

    public Optional<ImageUrl> getAvatarUrl() {
        return Optional.ofNullable(avatarUrl);
    }

    public GroupInfo withName(GroupName newName) {
        return new GroupInfo(newName, avatarUrl);
    }

    public GroupInfo withAvatar(ImageUrl newAvatarUrl) {
        return new GroupInfo(name, newAvatarUrl);
    }
}
