package com.fit.se.api.controller;

import com.fit.se.api.dto.request.OtpValidateRequest;
import com.fit.se.api.dto.request.SendOtpRequest;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.user.command.signup.ActiveCommandHandler;
import com.fit.se.application.user.query.otp.SendOTPQueryHandler;
import com.fit.se.infrastructure.otp.OtpService;
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
    private final ActiveCommandHandler activeCommandHandler;
    private final SendOTPQueryHandler sendOTPQueryHandler;

    @PostMapping("/send")
    public ApiResponse<?> sendOtp(@RequestBody SendOtpRequest request) {
        otpService.send(request.phone(), request.type());
        return ApiResponse.builder()
                .build();
    }

    @PostMapping("/resend")
    public ApiResponse<?> reSendOtp(@RequestBody String phone) {
//        otpService.reSend(phone);
        sendOTPQueryHandler.execute(phone);
        return ApiResponse.builder().build();
    }

    @PostMapping("/validate")
    public ApiResponse<?> validateOtp(@RequestBody OtpValidateRequest request) {
        boolean result = activeCommandHandler.execute(request.phone(), request.otp());
        return ApiResponse.builder()
                .data(result)
                .build();
    }
}
