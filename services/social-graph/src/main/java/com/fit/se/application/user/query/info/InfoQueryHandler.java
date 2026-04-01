package com.fit.se.application.user.query.info;

import com.fit.se.domain.block.BlockRelationRepository;
import com.fit.se.domain.user.UserRepository;
import com.fit.se.domain.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoQueryHandler {

    private final UserRepository userRepository;
    private final BlockRelationRepository blockRepository;

    public InfoResult execute(InfoQuery query) {
        validate(query);

        User targetUser = switch (query.type()) {
            case ID -> handleLookupById(query);
            case PHONE -> handleLookupByPhone(query);
            case QR -> handleLookupByQr(query);
        };

        ensureNotBlocked(query.current(), targetUser.getId());

        return toResult(targetUser);
    }

    private void validate(InfoQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query must not be null");
        }
        if (query.current() == null || query.current().isBlank()) {
            throw new IllegalArgumentException("Current user id must not be blank");
        }
        if (query.target() == null || query.target().isBlank()) {
            throw new IllegalArgumentException("Target must not be blank");
        }
        if (query.type() == null) {
            throw new IllegalArgumentException("Lookup type must not be null");
        }
    }

    private User handleLookupById(InfoQuery query) {
        return userRepository.findById(query.target())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private User handleLookupByPhone(InfoQuery query) {
        return userRepository.findByPhone(query.target())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private User handleLookupByQr(InfoQuery query) {
        return userRepository.findByQrToken(query.target())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void ensureNotBlocked(String currentUserId, String targetUserId) {
        if (blockRepository.existsActiveBlockBetween(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private InfoResult toResult(User user) {
        return InfoResult.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .bio(user.getProfile().getBio())
                .avatar(user.getAppearance().getAvatar())
                .background(user.getAppearance().getBackground())
                .theme(user.getAppearance().getTheme())
                .build();
    }
}