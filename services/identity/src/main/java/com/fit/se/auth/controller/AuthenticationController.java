package com.fit.se.auth.controller;

import com.fit.se.auth.application.changepass.ChangePasswordUseCase;
import com.fit.se.auth.application.forgotpass.ForgotPasswordUseCase;
import com.fit.se.auth.application.lock.LockAccountUseCase;
import com.fit.se.session.application.GetSessionUseCase;
import com.fit.se.auth.application.signin.SignInUseCase;
import com.fit.se.auth.application.signout.SignOutUseCase;
import com.fit.se.auth.application.signup.SignUpUseCase;
import com.fit.se.auth.application.token.generate.GenerateTokenResult;
import com.fit.se.auth.application.token.refresh.RefreshTokenQuery;
import com.fit.se.auth.application.token.refresh.RefreshTokenQueryHandler;
import com.fit.se.auth.application.token.revoke.RevokeAllCommand;
import com.fit.se.auth.dto.request.ChangePasswordRequest;
import com.fit.se.auth.dto.request.ConfirmResetPassword;
import com.fit.se.auth.dto.request.RefreshTokenRequest;
import com.fit.se.auth.dto.request.ResetPassword;
import com.fit.se.auth.dto.request.ResetPasswordRequest;
import com.fit.se.auth.dto.request.RevokeRequest;
import com.fit.se.auth.dto.request.SignInRequest;
import com.fit.se.auth.dto.request.SignUp;
import com.fit.se.auth.dto.request.SignUpRequest;
import com.fit.se.common.dto.response.ApiResponse;
import com.fit.se.session.domain.SessionState;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final RefreshTokenQueryHandler refreshTokenHandler;

    private final SignUpUseCase ucSignUp;
    private final SignInUseCase ucSignIn;
    private final SignOutUseCase ucSignOut;
    private final ForgotPasswordUseCase ucForgotPassword;
    private final ChangePasswordUseCase ucChangePassword;
    private final GetSessionUseCase ucGetActiveSession;

    private final LockAccountUseCase ucLockAccount;

    @PostMapping(value = "/signup/request")
    public ApiResponse<?> requestSignUp(@RequestBody SignUp request) {
        ucSignUp.request(request.phone());
        return ApiResponse.builder()
                .status(201)
                .message("Vui lÃ²ng xÃ¡c thá»±c OTP qua sá»‘ Ä‘iá»‡n thoáº¡i")
                .build();
    }

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequest request) {
        ucSignUp.execute(request.phone(), request.password(), request.otp());
        return ApiResponse.builder()
                .status(200)
                .message("ÄÄƒng kÃ½ thÃ nh cÃ´ng")
                .build();
    }

    @PostMapping("/signin")
    public ApiResponse<?> signIn(@Valid @RequestBody SignInRequest request,
                                 HttpServletRequest httpRequest) {
        GenerateTokenResult result = ucSignIn.execute(request.phone(), request.password(), httpRequest);
        return ApiResponse.builder()
                .status(200)
                .data(result)
                .message("ÄÄƒng nháº­p thÃ nh cÃ´ng")
                .build();
    }

    @PostMapping("/signout/all")
    public ApiResponse<?> signOutAll(@Valid @RequestBody RevokeAllCommand request) {
        ucSignOut.executeAll(request.refreshToken());
        return ApiResponse.builder()
                .status(200)
                .message("Báº¡n Ä‘Ã£ Ä‘Äƒng xuáº¥t táº¥t cáº£ cÃ¡c thiáº¿t bá»‹")
                .build();
    }

    @PostMapping("/signout/{sessId}")
    public ApiResponse<?> signOutClient(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable String sessId) {
        ucSignOut.executeClient(user.getUsername(), sessId);
        return ApiResponse.builder()
                .status(200)
                .message("Báº¡n Ä‘Ã£ Ä‘Äƒng xuáº¥t thiáº¿t bá»‹")
                .build();
    }

    @PostMapping("/signout")
    public ApiResponse<?> signOut(@Valid @RequestBody RevokeRequest request,
                                  HttpServletRequest httpRequest,
                                  @AuthenticationPrincipal UserDetails user) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        ucSignOut.execute(user.getUsername(), request.refreshToken(), ip, request.sessId());
        return ApiResponse.builder()
                .status(201)
                .message("Báº¡n Ä‘Ã£ Ä‘Äƒng xuáº¥t thiáº¿t bá»‹ nÃ y")
                .build();
    }

    @PostMapping("/qr/challenges/request")
    public ApiResponse<?> requestQRChallenges(HttpServletRequest httpRequest) {
        return ApiResponse.builder()
                .status(201)
                .data(ucSignIn.generateQR(httpRequest))
                .message("ÄÃ£ táº¡o mÃ£ QR")
                .build();
    }

    @PostMapping("/qr/challenges/{id}/approve")
    public ApiResponse<?> approveChallenge(@PathVariable("id") String id,
                                           @AuthenticationPrincipal UserDetails user) {
        ucSignIn.approveQR(id, user.getUsername());
        return ApiResponse.builder()
                .status(201)
                .message("ÄÄƒng nháº­p báº±ng mÃ£ QR")
                .build();
    }

    @PostMapping("/qr/challenges/{id}/exchange")
    public ApiResponse<?> exchangeChallenge(@PathVariable("id") String id,
                                            HttpServletRequest httpRequest) {
        return ApiResponse.builder()
                .status(201)
                .data(ucSignIn.exchangeQR(id, httpRequest))
                .message("ÄÄƒng nháº­p thÃ nh cÃ´ng")
                .build();
    }

    @PostMapping("/token/refresh")
    public ApiResponse<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request,
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
                .message("ÄÃ£ lÃ m má»›i token")
                .build();
    }

    @PostMapping("/password/reset/request")
    public ApiResponse<?> requestResetPassword(@RequestBody ResetPasswordRequest request) {
        ucForgotPassword.request(request.source(), request.type());
        return ApiResponse.builder()
                .status(200)
                .message("Vui lÃ²ng xÃ¡c nháº­n thay Ä‘á»•i")
                .build();
    }

    @PostMapping("/password/reset/confirm")
    public ApiResponse<?> confirmResetPassword(@RequestBody ConfirmResetPassword request) {
        String resetToken = ucForgotPassword.confirm(request.source(), request.type(), request.otp());
        return ApiResponse.builder()
                .status(200)
                .data(Map.of("resetToken", resetToken))
                .message("XÃ¡c nháº­n mÃ£ OTP thÃ nh cÃ´ng")
                .build();
    }

    @PostMapping("/password/reset")
    public ApiResponse<?> resetPassword(@RequestHeader("Authorization") String authorizationHeader,
                                        HttpServletRequest httpRequest,
                                        @RequestBody ResetPassword request) {
        GenerateTokenResult result = ucForgotPassword.execute(request.password(), authorizationHeader, httpRequest);

        return ApiResponse.builder()
                .status(200)
                .data(result)
                .message("Máº­t kháº©u Ä‘Ã£ Ä‘Æ°á»£c lÃ m má»›i")
                .build();
    }

    @PostMapping("/password/change")
    public ApiResponse<?> changePassword(@AuthenticationPrincipal UserDetails user,
                                         @RequestBody ChangePasswordRequest request) {
        ucChangePassword.execute(user.getUsername(), request.newPass(), request.oldPass());
        ucSignOut.executeAll(request.refreshToken());
        return ApiResponse.builder()
                .status(200)
                .message("Thay Ä‘á»•i máº­t kháº©u má»›i thÃ nh cÃ´ng")
                .build();
    }

    @GetMapping("/sessions/active")
    public ApiResponse<?> getActiveSession(@AuthenticationPrincipal UserDetails user) {
        return ApiResponse.builder()
                .status(200)
                .data(ucGetActiveSession.execute(user.getUsername(), SessionState.ACTIVE))
                .build();
    }

    @GetMapping("/sessions/unactive")
    public ApiResponse<?> getUnactiveSession(@AuthenticationPrincipal UserDetails user) {
        return ApiResponse.builder()
                .status(200)
                .data(ucGetActiveSession.executeWithout(user.getUsername(), SessionState.ACTIVE))
                .build();
    }

    @PostMapping("/lock")
    public ApiResponse<?> lockAccount(@AuthenticationPrincipal UserDetails user) {
        ucLockAccount.execute(user.getUsername());
        return ApiResponse.builder().build();
    }
}

