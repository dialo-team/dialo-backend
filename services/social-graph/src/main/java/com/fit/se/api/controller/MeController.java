package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.blocks.query.mine.MyBlockQuery;
import com.fit.se.application.blocks.query.mine.MyBlockQueryHandler;
import com.fit.se.application.friendship.query.mine.MyFriendQuery;
import com.fit.se.application.friendship.query.mine.MyFriendQueryHandler;
import com.fit.se.application.friendship.query.pending.MyPendingFriendRequestsQuery;
import com.fit.se.application.friendship.query.pending.MyPendingFriendRequestsQueryHandler;
import com.fit.se.application.friendship.query.pending.MySentFriendRequestsQueryHandler;
import com.fit.se.application.user.command.appearance.UpdateAppearanceCommand;
import com.fit.se.application.user.command.appearance.UpdateAppearanceCommandHandler;
import com.fit.se.application.user.command.avatar.UpdateAvatarCommand;
import com.fit.se.application.user.command.avatar.UpdateAvatarCommandHandler;
import com.fit.se.application.user.command.background.UpdateBackgroundCommand;
import com.fit.se.application.user.command.background.UpdateBackgroundCommandHandler;
import com.fit.se.application.user.command.basicinfo.UpdateBasicInfoCommand;
import com.fit.se.application.user.command.basicinfo.UpdateBasicInfoCommandHandler;
import com.fit.se.application.user.command.bio.UpdateBioCommand;
import com.fit.se.application.user.command.bio.UpdateBioCommandHandler;
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
import com.fit.se.infrastructure.config.context.HolderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final UpdateAvatarCommandHandler updateAvatarHandler;
    private final UpdateBackgroundCommandHandler updateBackgroundHandler;
    private final UpdateBioCommandHandler updateBioHandler;
    private final UpdateBasicInfoCommandHandler updateBasicInfoHandler;

    private final MyPendingFriendRequestsQueryHandler myPendingFriendRequestsHandler;
    private final MySentFriendRequestsQueryHandler mySentFriendRequestsHandler;

    @GetMapping("/friend-requests")
    public ApiResponse<?> myPendingFriendRequests(
            @RequestHeader("X-User-Id") String currentUserId
    ) {
        MyPendingFriendRequestsQuery query = MyPendingFriendRequestsQuery.builder()
                .current(currentUserId)
                .build();

        return ApiResponse.builder()
                .data(myPendingFriendRequestsHandler.execute(query))
                .build();
    }

    @GetMapping("/friend-requests/sent")
    public ApiResponse<?> mySentFriendRequests(
    ) {
        MyPendingFriendRequestsQuery query = MyPendingFriendRequestsQuery.builder()
                .current(HolderContext.getRequiredUserId())
                .build();

        return ApiResponse.builder()
                .data(mySentFriendRequestsHandler.execute(query))
                .build();
    }

    @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> updateAvatar(
            @RequestPart("file") MultipartFile file
    ) {
        UpdateAvatarCommand command = UpdateAvatarCommand.builder()
                .current(HolderContext.getRequiredUserId())
                .build();

        updateAvatarHandler.execute(command, file);

        return ApiResponse.builder()
                .status(200)
                .message("Avatar updated successfully")
                .build();
    }

    @PatchMapping(value = "/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> updateBackground(
            @RequestPart("file") MultipartFile file
    ) {
        UpdateBackgroundCommand command = UpdateBackgroundCommand.builder()
                .current(HolderContext.getRequiredUserId())
                .build();

        updateBackgroundHandler.execute(command, file);

        return ApiResponse.builder()
                .status(200)
                .message("Background updated successfully")
                .build();
    }

    @PatchMapping("/bio")
    public ApiResponse<?> updateBio(
            @RequestBody UpdateBioRequest request
    ) {
        UpdateBioCommand command = UpdateBioCommand.builder()
                .current(HolderContext.getRequiredUserId())
                .bio(request.bio())
                .build();

        updateBioHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Bio updated successfully")
                .build();
    }

    @PatchMapping("/basic-info")
    public ApiResponse<?> updateBasicInfo(
            @RequestBody UpdateBasicInfoRequest request
    ) {
        UpdateBasicInfoCommand command = UpdateBasicInfoCommand.builder()
                .current(HolderContext.getRequiredUserId())
                .userName(request.userName())
                .dob(request.dob())
                .gender(request.gender())
                .build();

        updateBasicInfoHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("Basic info updated successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<?> myInfo() {
        MyInfoQuery query = MyInfoQuery.builder().current(HolderContext.getRequiredUserId()).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @GetMapping("/friends")
    public ApiResponse<?> myFriends() {
        MyFriendQuery query = MyFriendQuery.builder().current(HolderContext.getRequiredUserId()).build();
        return ApiResponse.builder()
                .data(myFriendHandler.execute(query))
                .build();
    }

    @GetMapping("/blocks")
    public ApiResponse<?> myBlocks() {
        MyBlockQuery query = MyBlockQuery.builder().current(HolderContext.getRequiredUserId()).build();
        return ApiResponse.builder()
                .data(myBlockHandler.execute(query))
                .build();
    }

    @GetMapping("/qr")
    public ApiResponse<?> myQr() {
        MyInfoQuery query = MyInfoQuery.builder().current(HolderContext.getRequiredUserId()).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @PatchMapping("/profile")
    public ApiResponse<?> updateProfile(
            @RequestBody UpdateProfileRequest request
    ) {
        UpdateProfileCommand command = UpdateProfileCommand.builder()
                .current(HolderContext.getRequiredUserId())
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
    public ApiResponse<?> myAppearance() {
        MyInfoQuery query = MyInfoQuery.builder().current(HolderContext.getRequiredUserId()).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @PatchMapping("/appearance")
    public ApiResponse<?> updateAppearance(
            @RequestBody UpdateAppearanceRequest request
    ) {
        UpdateAppearanceCommand command = UpdateAppearanceCommand.builder()
                .current(HolderContext.getRequiredUserId())
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
    public ApiResponse<?> myPrivacy() {
        MyInfoQuery query = MyInfoQuery.builder().current(HolderContext.getRequiredUserId()).build();
        return ApiResponse.builder()
                .data(myInfoHandler.execute(query))
                .build();
    }

    @PatchMapping("/privacy")
    public ApiResponse<?> updatePrivacy(
            @RequestBody UpdatePrivacyRequest request
    ) {
        UpdatePrivacyCommand command = UpdatePrivacyCommand.builder()
                .current(HolderContext.getRequiredUserId())
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
            @PathVariable String targetId,
            @RequestBody OverridePrivacyRequest request
    ) {
        OverridePrivacyCommand command = OverridePrivacyCommand.builder()
                .current(HolderContext.getRequiredUserId())
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
            @PathVariable String targetId,
            @RequestParam("key") String key
    ) {
        RemovePrivacyOverrideCommand command = RemovePrivacyOverrideCommand.builder()
                .current(HolderContext.getRequiredUserId())
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
    public ApiResponse<?> customizeQr(@RequestBody CustomizeQrRequest request) {
        CustomizeQrCommand command = CustomizeQrCommand.builder()
                .current(HolderContext.getRequiredUserId())
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
    public ApiResponse<?> rotateQr() {
        RotateQrCommand command = RotateQrCommand.builder()
                .current(HolderContext.getRequiredUserId())
                .build();

        rotateQrHandler.execute(command);

        return ApiResponse.builder()
                .status(200)
                .message("QR rotated successfully")
                .build();
    }


}