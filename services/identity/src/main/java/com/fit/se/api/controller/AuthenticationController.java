package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.application.qr.approve.QRApproveCommand;
import com.fit.se.application.qr.approve.QRApproveCommandHandler;
import com.fit.se.application.qr.exchange.QRExchangeCommand;
import com.fit.se.application.qr.exchange.QRExchangeCommandHandler;
import com.fit.se.application.qr.generate.QRChallengeQuery;
import com.fit.se.application.qr.generate.QRChallengeQueryHandler;
import com.fit.se.application.token.generate.GenerateTokenQuery;
import com.fit.se.application.token.generate.GenerateTokenQueryHandler;
import com.fit.se.application.token.generate.VerifyOTPForSignInCommand;
import com.fit.se.application.token.generate.VerifyOTPForSignInCommandHandler;
import com.fit.se.application.token.refresh.RefreshTokenQuery;
import com.fit.se.application.token.refresh.RefreshTokenQueryHandler;
import com.fit.se.application.token.revoke.RevokeAllCommand;
import com.fit.se.application.token.revoke.RevokeCommand;
import com.fit.se.application.token.revoke.RevokeTokenHandler;
import com.fit.se.application.user.command.signup.SignUpCommand;
import com.fit.se.application.user.command.signup.SignUpCommandHandler;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.user.command.verify.VerifyOtpCommandHandler;
import com.fit.se.application.user.command.verify.VerifyOTPCommand;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final SignUpCommandHandler signUpHandler;
    private final VerifyOtpCommandHandler verifyOtpCommandHandler;
    private final GenerateTokenQueryHandler generateTokenHandler;
    private final RevokeTokenHandler revokeTokenHandler;
    private final RefreshTokenQueryHandler refreshTokenHandler;
    private final QRChallengeQueryHandler qrChallengeRequestHandler;
    private final QRApproveCommandHandler qrApproveCommandHandler;
    private final QRExchangeCommandHandler qrExchangeCommandHandler;
    private final VerifyOTPForSignInCommandHandler verifyOTPForSignInCommandHandler;

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignInRequest request) {
        SignUpCommand cmd = SignUpCommand.builder()
                .phone(request.phone())
                .password(request.password())
                .build();
        signUpHandler.execute(cmd);
        return ApiResponse.builder()
                .status(201)
                .message("account is created")
                .build();
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/signup/verify")
    public ApiResponse<?> verifyOTPForSignUp(@RequestBody VerifyOTPRequest request) {
        VerifyOTPCommand cmd = VerifyOTPCommand.builder()
                .phone(request.phone())
                .otp(request.otp())
                .build();
        boolean result = verifyOtpCommandHandler.execute(cmd);
        return ApiResponse.builder()
                .status(200)
                .data(Map.of("result", result))
                .message(result ? "Đăng ký thành công": "Đăng ký thất bại, hãy thử lại!")
                .build();
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/signin")
    public ApiResponse<?> signIn(@RequestBody SignInRequest request,
                                 HttpServletRequest httpRequest) {
        String agent = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        GenerateTokenQuery query = GenerateTokenQuery.builder()
                .phone(request.phone())
                .password(request.password())
                .deviceName(agent)
                .ipAddress(ip)
                .loginMethod("PASSWORD")
                .build();

        generateTokenHandler.execute(query);
        return ApiResponse.builder()
                .status(200)
                .message("")
                .build();
    }

    @PostMapping("/signin/verify")
    public ApiResponse<?> verifyOTPForSignIn(@RequestBody VerifyOTPRequest request,
                                             HttpServletRequest httpRequest) {
        VerifyOTPForSignInCommand cmd = VerifyOTPForSignInCommand.builder()
                .phone(request.phone())
                .otp(request.otp())
                .httpRequest(httpRequest)
                .build();
        return ApiResponse.builder()
                .status(200)
                .data(verifyOTPForSignInCommandHandler.execute(cmd))
                .build();
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(@RequestBody RefreshTokenRequest request,
                                       HttpServletRequest httpRequest) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        RefreshTokenQuery query = RefreshTokenQuery.builder()
                .refreshToken(request.refreshToken())
                .ipAddress(ip)
                .build();
        return ApiResponse.builder()
                .status(200)
                .data(refreshTokenHandler.execute(query))
                .build();
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/signout")
    public ApiResponse<?> signOut(@RequestBody RevokeRequest request,
                                  HttpServletRequest httpRequest,
                                  @AuthenticationPrincipal UserDetails user) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        RevokeCommand cmd = RevokeCommand.builder()
                .subject(user.getUsername())
                .refreshToken(request.refreshToken())
                .ipAddress(ip)
                .build();
        revokeTokenHandler.execute(cmd);
        return ApiResponse.builder()
                .status(201)
                .message("account is logout")
                .build();
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/signout/all-client")
    public ApiResponse<?> signOutAllClient(@RequestBody RevokeAllCommand request) {
        RevokeAllCommand cmd = RevokeAllCommand.builder()
                .refreshToken(request.refreshToken())
                .build();
        revokeTokenHandler.execute(cmd);
        return ApiResponse.builder()
                .status(200)
                .build();
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("/qr/challenges")
    public ApiResponse<?> qrChallenges(HttpServletRequest httpRequest) {
        String agent = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        QRChallengeQuery query = QRChallengeQuery.builder()
                .ipAddress(ip)
                .agent(agent)
                .build();
        return ApiResponse.builder()
                .status(201)
                .data(qrChallengeRequestHandler.execute(query))
                .message("account is logout")
                .build();
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("/qr/challenges/{id}/approve")
    public ApiResponse<?> qrChallengesApprove(@PathVariable("id") String id,
                                              HttpServletRequest httpRequest,
                                              @AuthenticationPrincipal UserDetails user) {
        String agent = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        QRApproveCommand cmd = QRApproveCommand.builder()
                .challengeId(id)
                .userId(user.getUsername())
                .ipAddress(ip)
                .agent(agent)
                .build();
        qrApproveCommandHandler.execute(cmd);
        return ApiResponse.builder()
                .status(201)
                .message("account is logout")
                .build();
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("/qr/challenges/{id}/exchange")
    public ApiResponse<?> qrChallengesExchange(@PathVariable("id") String id,
                                               HttpServletRequest httpRequest) {
        String agent = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        QRExchangeCommand cmd = QRExchangeCommand.builder()
                .challengeId(id)
                .ipAddress(ip)
                .agent(agent)
                .build();

        return ApiResponse.builder()
                .status(201)
                .data(qrExchangeCommandHandler.execute(cmd))
                .message("account is logout")
                .build();
    }
}
