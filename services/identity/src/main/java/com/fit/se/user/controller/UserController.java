package com.fit.se.user.controller;

import com.fit.se.user.dto.request.*;
import com.fit.se.user.application.command.changeemail.UEmailCommand;
import com.fit.se.user.application.command.changeemail.UEmailCommandHandler;
import com.fit.se.common.dto.response.ApiResponse;
import com.fit.se.user.application.command.resetpass.ResetPassCommand;
import com.fit.se.user.application.command.resetpass.ResetPassHandler;
import com.fit.se.user.application.command.updatephone.UPhoneCommand;
import com.fit.se.user.application.command.updatephone.UPhoneCommandHandler;
import com.fit.se.user.application.command.updatephone.VerifyOTPUPhoneCommand;
import com.fit.se.user.application.command.updatephone.VerifyPhoneCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class UserController {
    private final UEmailCommandHandler uEmailHandler;
    private final UPhoneCommandHandler uPhoneCommandHandler;
    private final VerifyPhoneCommandHandler verifyPhoneCommandHandler;
    private final ResetPassHandler resetPassHandler;


    @PostMapping
    public ApiResponse<?> resetPassword(Authentication authentication,
                                        ResetPassRequest request) {
        ResetPassCommand cmd = ResetPassCommand.builder()
                .newPass(request.newPass())
                .build();
        resetPassHandler.execute(cmd);
        return ApiResponse.builder()
                .status(200)
                .message("password is updated")
                .build();
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
}

