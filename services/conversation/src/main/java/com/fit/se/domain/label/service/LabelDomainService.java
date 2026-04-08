package com.fit.se.domain.label.service;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.label.valueobject.LabelName;
import com.fit.se.domain.label.aggregate.UserConversationLabel;
import com.fit.se.domain.label.exception.LabelAlreadyExistsException;
import com.fit.se.domain.label.exception.LabelOwnershipException;
import com.fit.se.domain.label.repository.UserConversationLabelRepository;
import com.fit.se.domain.label.aggregate.LabelColor;

import java.util.List;
import java.util.Objects;

public class LabelDomainService {
    public static final String PROTECTED_STRANGER_LABEL = "Tin nhắn từ người lạ";

    private final UserConversationLabelRepository labelRepository;

    public LabelDomainService(UserConversationLabelRepository labelRepository) {
        this.labelRepository = Objects.requireNonNull(labelRepository, "labelRepository must not be null");
    }

    public UserConversationLabel createCustomLabel(UserId ownerId, LabelName labelName, LabelColor color) {
        ensureUnique(ownerId, labelName);
        return UserConversationLabel.customLabel(ownerId, labelName, color);
    }

    public List<UserConversationLabel> bootstrapDefaultLabels(UserId ownerId) {
        return List.of(
                createDefaultIfMissing(ownerId, "Khách hàng", "#FF9800", true),
                createDefaultIfMissing(ownerId, "Gia đình", "#E91E63", true),
                createDefaultIfMissing(ownerId, "Công việc", "#3F51B5", true),
                createDefaultIfMissing(ownerId, "Bạn bè", "#4CAF50", true),
                createDefaultIfMissing(ownerId, "Trả lời sau", "#795548", true),
                createDefaultIfMissing(ownerId, "Đồng nghiệp", "#2196F3", true),
                createDefaultIfMissing(ownerId, "Môn học", "#9C27B0", true),
                createDefaultIfMissing(ownerId, PROTECTED_STRANGER_LABEL, "#9E9E9E", false)
        ).stream().filter(Objects::nonNull).toList();
    }

    public void ensureOwnership(UserConversationLabel label, UserId expectedOwnerId) {
        if (!label.getOwnerId().equals(expectedOwnerId)) {
            throw new LabelOwnershipException("Label does not belong to current user");
        }
    }

    public void ensureUnique(UserId ownerId, LabelName labelName) {
        if (labelRepository.existsByOwnerIdAndName(ownerId, labelName)) {
            throw new LabelAlreadyExistsException("Label name already exists for this user");
        }
    }

    private UserConversationLabel createDefaultIfMissing(UserId ownerId, String name, String color, boolean deletable) {
        LabelName labelName = new LabelName(name);
        if (labelRepository.existsByOwnerIdAndName(ownerId, labelName)) {
            return null;
        }
        return UserConversationLabel.defaultLabel(ownerId, labelName, new LabelColor(color), deletable);
    }
}
