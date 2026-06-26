package com.fit.se.controller;

import com.fit.se.dto.UserProfileRequest;
import com.fit.se.dto.UserProfileResponse;
import com.fit.se.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping
    public UserProfileResponse upsert(@Valid @RequestBody UserProfileRequest request) {
        return userProfileService.upsert(request);
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getById(@PathVariable String userId) {
        return userProfileService.getById(userId);
    }
}
