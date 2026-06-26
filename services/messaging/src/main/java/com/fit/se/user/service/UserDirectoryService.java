package com.fit.se.user.service;

import com.fit.se.user.domain.UserProfileDocument;
import com.fit.se.event.model.UserCreatedEvent;
import com.fit.se.common.exception.ResourceNotFoundException;
import com.fit.se.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserDirectoryService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileDocument getRequired(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Id nguoi dung khong duoc de trong");
        }
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user profile: " + userId));
    }

    public UserProfileDocument getOrCreate(String userId) {
        return userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    UserProfileDocument profile = new UserProfileDocument();
                    profile.setId(userId);
                    profile.setDisplayName(userId);
                    profile.setAvatarUrl(null);
                    profile.setCreatedAt(now);
                    profile.setUpdatedAt(now);
                    return userProfileRepository.save(profile);
                });
    }

    @RabbitListener(queues = "messaging.user.created")
    public void handleUserCreated(UserCreatedEvent event) {
        if (event == null || event.userId() == null || event.userId().isBlank()) {
            return;
        }

        Instant now = Instant.now();
        UserProfileDocument profile = userProfileRepository.findById(event.userId()).orElseGet(UserProfileDocument::new);
        profile.setId(event.userId());
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(now);
        }
        if (profile.getDisplayName() == null || profile.getDisplayName().isBlank() || profile.getDisplayName().equals(profile.getId())) {
            profile.setDisplayName(event.phone() == null || event.phone().isBlank() ? event.userId() : event.phone());
        }
        if (profile.getAvatarUrl() == null) {
            profile.setAvatarUrl(null);
        }
        profile.setUpdatedAt(now);
        userProfileRepository.save(profile);
    }
}
