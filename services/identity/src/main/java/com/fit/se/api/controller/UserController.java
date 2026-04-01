package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.application.token.revoke.RevokeAllCommand;
import com.fit.se.application.token.revoke.RevokeTokenHandler;
import com.fit.se.application.user.command.changeemail.UEmailCommand;
import com.fit.se.application.user.command.changeemail.UEmailCommandHandler;
import com.fit.se.application.user.command.changepass.UPasswordCommand;
import com.fit.se.application.user.command.changepass.UPasswordCommandHandler;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.user.command.updatephone.UPhoneCommand;
import com.fit.se.application.user.command.updatephone.UPhoneCommandHandler;
import com.fit.se.application.user.command.updatephone.VerifyOTPUPhoneCommand;
import com.fit.se.application.user.command.updatephone.VerifyPhoneCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class UserController {
    private final UPasswordCommandHandler uPasswordHandler;
    private final UEmailCommandHandler uEmailHandler;
    private final UPhoneCommandHandler uPhoneCommandHandler;
    private final RevokeTokenHandler revokeTokenHandler;
    private final VerifyPhoneCommandHandler verifyPhoneCommandHandler;

    @PutMapping("/{id}/password")
    public ApiResponse<?> updatePassword(@PathVariable String id,
                                         @RequestBody ChangePasswordRequest request) {
        UPasswordCommand cmd = UPasswordCommand.builder()
                .userId(id)
                .newPass(request.newPass())
                .build();
        RevokeAllCommand revokeCmd = RevokeAllCommand.builder()
                .refreshToken(request.refreshToken())
                .build();
        uPasswordHandler.execute(cmd);
        revokeTokenHandler.execute(revokeCmd);
        return ApiResponse.builder()
                .status(201)
                .message("password is updated")
                .build();
    }

    @PostMapping("/{id}/password/verify")
    public ApiResponse<?> verifyPassword() {
        return ApiResponse.builder().build();
    }

    @PutMapping("/{id}/email")
    public ApiResponse<?> updateEmail(@PathVariable String id,
                                      @RequestBody ChangeEmailRequest request) {
        UEmailCommand cmd = UEmailCommand.builder()
                .userId(id)
                .newEmail(request.newEmail())
                .build();

        uEmailHandler.execute(cmd);
        return ApiResponse.builder()
                .status(200)
                .message("email is updated")
                .build();
    }

    @PostMapping("/{id}/email/verify")
    public ApiResponse<?> verifyEmail() {
        return ApiResponse.builder()
                .build();
    }

    @PutMapping("/{id}/phone")
    public ApiResponse<?> updatePhone(@PathVariable String id,
                                      @RequestBody UPhoneRequest request) {
        UPhoneCommand cmd = UPhoneCommand.builder()
                .userId(id)
                .newPhone(request.newPhone())
                .build();
        uPhoneCommandHandler.execute(cmd);
        return ApiResponse.builder()
                .status(200)
                .message("phone is updated")
                .build();
    }

    @PostMapping("/{id}/phone/verify")
    public ApiResponse<?> verifyUPhone(@PathVariable String id,
                                       @RequestBody VerifyOTPUPhoneRequest request) {
        VerifyOTPUPhoneCommand cmd = VerifyOTPUPhoneCommand.builder()
                .userId(id)
                .otp(request.otp())
                .build();
        verifyPhoneCommandHandler.execute(cmd);
        return ApiResponse.builder()
                .status(200)
                .message("phone is updated")
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<?> getMyInfo() {
        return ApiResponse.builder()
                .build();
    }
}
