package com.fit.se.controller;

import com.fit.se.dto.request.OtpValidateRequest;
import com.fit.se.dto.request.SendOtpRequest;
import com.fit.se.dto.response.ApiResponse;
import com.fit.se.service.AccountService;
import com.fit.se.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;
    private final AccountService accountService;

    @PostMapping("/send")
    public ApiResponse<?> sendOtp(@RequestBody SendOtpRequest request) {
        otpService.send(request.phone(), request.type());
        return ApiResponse.builder()
                .build();
    }

    @PostMapping("/resend")
    public ApiResponse<?> reSendOtp(@RequestBody String phone) {
        otpService.reSend(phone);
        return ApiResponse.builder().build();
    }

    @PostMapping("/validate")
    public ApiResponse<?> validateOtp(@RequestBody OtpValidateRequest request) {
        if(otpService.validate(request)) {
            accountService.activate(request.phone());
        }
        return ApiResponse.builder()
                .data(otpService.validate(request))
                .build();
    }
}
