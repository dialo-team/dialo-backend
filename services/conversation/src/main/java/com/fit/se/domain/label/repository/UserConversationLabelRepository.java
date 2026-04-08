package com.fit.se.domain.label.repository;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.label.valueobject.LabelName;
import com.fit.se.domain.label.aggregate.UserConversationLabel;
import com.fit.se.domain.label.valueobject.LabelId;

import java.util.List;
import java.util.Optional;

public interface UserConversationLabelRepository {
    Optional<UserConversationLabel> findById(LabelId labelId);

    List<UserConversationLabel> findAllByOwnerId(UserId ownerId);

    Optional<UserConversationLabel> findByOwnerIdAndName(UserId ownerId, LabelName labelName);

    boolean existsByOwnerIdAndName(UserId ownerId, LabelName labelName);

    UserConversationLabel save(UserConversationLabel label);

    void delete(UserConversationLabel label);
}
