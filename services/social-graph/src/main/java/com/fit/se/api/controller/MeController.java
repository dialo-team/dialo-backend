package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.blocks.query.mine.MyBlockQuery;
import com.fit.se.application.blocks.query.mine.MyBlockQueryHandler;
import com.fit.se.application.friendship.query.mine.MyFriendQuery;
import com.fit.se.application.friendship.query.mine.MyFriendQueryHandler;
import com.fit.se.application.user.command.appearance.UpdateAppearanceCommand;
import com.fit.se.application.user.command.appearance.UpdateAppearanceCommandHandler;
import com.fit.se.application.user.command.privacy.OverridePrivacyCommand;
import com.fit.se.application.user.command.privacy.OverridePrivacyCommandHandler;
import com.fit.se.application.user.command.privacy.RemovePrivacyOverrideCommand;
import com.fit.se.application.user.command.privacy.RemovePrivacyOverrideCommandHandler;
import com.fit.se.application.user.command.privacy.UpdatePrivacyCommand;
import com.fit.se.application.user.command.privacy.UpdatePrivacyCommandHandler;
import com.fit.se.application.user.command.profile.UpdateProfileCommand;
import com.fit.se.application.user.command.profile.UpdateProfileCommandHandler;
import com.fit.se.application.user.command.qr.CustomizeQrCommand;
import com.fit.se.application.user.command.qr.CustomizeQrCommandHandler;
import com.fit.se.application.user.command.qr.RotateQrCommand;
import com.fit.se.application.user.command.qr.RotateQrCommandHandler;
import com.fit.se.application.user.query.mine.MyInfoQuery;
import com.fit.se.application.user.query.mine.MyInfoQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final MyFriendQueryHandler myFriendHandler;
    private final MyBlockQueryHandler myBlockHandler;
    private final MyInfoQueryHandler myInfoHandler;

    private final UpdateProfileCommandHandler updateProfileHandler;
    private final UpdateAppearanceCommandHandler updateAppearanceHandler;
    private final UpdatePrivacyCommandHandler updatePrivacyHandler;
    private final OverridePrivacyCommandHandler overridePrivacyHandler;
    private final RemovePrivacyOverrideCommandHandler removePrivacyOverrideHandler;
    private final CustomizeQrCommandHandler customizeQrHandler;
    private final RotateQrCommandHandler rotateQrHandler;

    @GetMapping
    public ApiResponse<?> myInfo(@RequestHeader("X-User-Id") String currentUserId) {
        MyInfoQuery query = MyInfoQuery.builder().current(currentUserId).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @GetMapping("/friends")
    public ApiResponse<?> myFriends(@RequestHeader("X-User-Id") String currentUserId) {
        MyFriendQuery query = MyFriendQuery.builder().current(currentUserId).build();
        return ApiResponse.builder()
                .data(myFriendHandler.execute(query))
                .build();
    }

    @GetMapping("/blocks")
    public ApiResponse<?> myBlocks(@RequestHeader("X-User-Id") String currentUserId) {
        MyBlockQuery query = MyBlockQuery.builder().current(currentUserId).build();
        return ApiResponse.builder()
                .data(myBlockHandler.execute(query))
                .build();
    }

    @GetMapping("/qr")
    public ApiResponse<?> myQr(@RequestHeader("X-User-Id") String currentUserId) {
        MyInfoQuery query = MyInfoQuery.builder().current(currentUserId).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @PatchMapping("/profile")
    public ApiResponse<?> updateProfile(
            @RequestHeader("X-User-Id") String currentUserId,
            @RequestBody UpdateProfileRequest request
    ) {
        UpdateProfileCommand command = UpdateProfileCommand.builder()
                .current(currentUserId)
                .bio(request.bio())
                .gender(request.gender())
                .dob(request.dob())
                .build();

        updateProfileHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Profile updated successfully")
                .build();
    }

    @GetMapping("/appearance")
    public ApiResponse<?> myAppearance(@RequestHeader("X-User-Id") String currentUserId) {
        MyInfoQuery query = MyInfoQuery.builder().current(currentUserId).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @PatchMapping("/appearance")
    public ApiResponse<?> updateAppearance(
            @RequestHeader("X-User-Id") String currentUserId,
            @RequestBody UpdateAppearanceRequest request
    ) {
        UpdateAppearanceCommand command = UpdateAppearanceCommand.builder()
                .current(currentUserId)
                .avatar(request.avatar())
                .background(request.background())
                .theme(request.theme())
                .build();

        updateAppearanceHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Appearance updated successfully")
                .build();
    }

    @GetMapping("/privacy")
    public ApiResponse<?> myPrivacy(@RequestHeader("X-User-Id") String currentUserId) {
        MyInfoQuery query = MyInfoQuery.builder().current(currentUserId).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @PatchMapping("/privacy")
    public ApiResponse<?> updatePrivacy(
            @RequestHeader("X-User-Id") String currentUserId,
            @RequestBody UpdatePrivacyRequest request
    ) {
        UpdatePrivacyCommand command = UpdatePrivacyCommand.builder()
                .current(currentUserId)
                .birthdayVisibility(request.birthdayVisibility())
                .birthdayNotifyFriends(request.birthdayNotifyFriends())
                .build();

        updatePrivacyHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Privacy updated successfully")
                .build();
    }

    @PutMapping("/privacy/overrides/{targetId}")
    public ApiResponse<?> overridePrivacy(
            @RequestHeader("X-User-Id") String currentUserId,
            @PathVariable String targetId,
            @RequestBody OverridePrivacyRequest request
    ) {
        OverridePrivacyCommand command = OverridePrivacyCommand.builder()
                .current(currentUserId)
                .targetId(targetId)
                .key(request.key())
                .decision(request.decision())
                .build();

        overridePrivacyHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Privacy override updated successfully")
                .build();
    }

    @DeleteMapping("/privacy/overrides/{targetId}")
    public ApiResponse<?> unOverridePrivacy(
            @RequestHeader("X-User-Id") String currentUserId,
            @PathVariable String targetId,
            @RequestParam("key") String key
    ) {
        RemovePrivacyOverrideCommand command = RemovePrivacyOverrideCommand.builder()
                .current(currentUserId)
                .targetId(targetId)
                .key(key)
                .build();

        removePrivacyOverrideHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Privacy override removed successfully")
                .build();
    }

    @PatchMapping("/qr")
    public ApiResponse<?> customizeQr(
            @RequestHeader("X-User-Id") String currentUserId,
            @RequestBody CustomizeQrRequest request
    ) {
        CustomizeQrCommand command = CustomizeQrCommand.builder()
                .current(currentUserId)
                .title(request.title())
                .description(request.description())
                .color(request.color())
                .build();

        customizeQrHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("QR customized successfully")
                .build();
    }

    @PostMapping("/qr/rotate")
    public ApiResponse<?> rotateQr(@RequestHeader("X-User-Id") String currentUserId) {
        RotateQrCommand command = RotateQrCommand.builder()
                .current(currentUserId)
                .build();

        rotateQrHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("QR rotated successfully")
                .build();
    }
}