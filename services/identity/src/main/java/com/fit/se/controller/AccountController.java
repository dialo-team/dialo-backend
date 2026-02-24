package com.fit.se.controller;

import com.fit.se.dto.request.DevicesCheckRequest;
import com.fit.se.dto.request.UpEmailRequest;
import com.fit.se.dto.request.UpPassRequest;
import com.fit.se.dto.response.ApiResponse;
import com.fit.se.service.AccountService;
import com.fit.se.service.DeviceSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final DeviceSessionService deviceSessionService;

    @PutMapping("/{id}/password")
    public ApiResponse<?> updatePassword(@PathVariable String id, @RequestBody UpPassRequest request) {
        accountService.updatePassword(id, request);
        return ApiResponse.builder()
                .status(201)
                .message("password is updated")
                .build();
    }

    @PutMapping("/{id}/email")
    public ApiResponse<?> updateEmail(@PathVariable String id, @RequestBody UpEmailRequest request) {
        accountService.updateEmail(id, request);
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
