package com.fit.se.api.controller;

import com.fit.se.api.dto.request.ChangeEmailRequest;
import com.fit.se.api.dto.request.ChangePasswordRequest;
import com.fit.se.application.user.command.changeemail.ChangeEmailCommand;
import com.fit.se.application.user.command.changeemail.ChangeEmailHandler;
import com.fit.se.application.user.command.changepass.ChangePasswordCommand;
import com.fit.se.application.user.command.changepass.ChangePasswordHandler;
import com.fit.se.api.dto.request.DevicesCheckRequest;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.user.query.DeviceSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final DeviceSessionService deviceSessionService;
    private final ChangePasswordHandler changePasswordHandler;
    private final ChangeEmailHandler changeEmailHandler;

    @PutMapping("/{id}/password")
    public ApiResponse<?> updatePassword(@PathVariable String id, @RequestBody ChangePasswordRequest request) {
        ChangePasswordCommand cmd = ChangePasswordCommand.builder()
                .accId(id)
                .newPass(request.newPass())
                .build();
        changePasswordHandler.execute(cmd);
        return ApiResponse.builder()
                .status(201)
                .message("password is updated")
                .build();
    }

    @PutMapping("/{id}/email")
    public ApiResponse<?> updateEmail(@PathVariable String id, @RequestBody ChangeEmailRequest request) {
        ChangeEmailCommand cmd = ChangeEmailCommand.builder()
                .accId(id)
                .newEmail(request.newEmail())
                .build();

        changeEmailHandler.execute(cmd);
        return ApiResponse.builder()
                .status(201)
                .message("email is updated")
                .build();
    }

    @GetMapping("/devices")
    public ApiResponse<?> getLoggedInDevices(@RequestBody DevicesCheckRequest request) {
        var devices = deviceSessionService.getLoggedInDevices(request.accId());
        log.info(devices.toString());

        return ApiResponse.builder()
                .status(201)
                .data(devices)
                .message("all devices logged in")
                .build();
    }
}
