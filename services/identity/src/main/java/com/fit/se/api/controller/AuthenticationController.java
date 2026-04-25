package com.fit.se.api.controller;

import com.fit.se.api.dto.request.*;
import com.fit.se.application.changepass.ChangePasswordUseCase;
import com.fit.se.application.forgotpass.ForgotPasswordUseCase;
import com.fit.se.application.lock.LockAccountUseCase;
import com.fit.se.application.session.GetSessionUseCase;
import com.fit.se.application.signin.SignInUseCase;
import com.fit.se.application.signout.SignOutUseCase;
import com.fit.se.application.token.generate.*;
import com.fit.se.application.token.refresh.RefreshTokenQuery;
import com.fit.se.application.token.refresh.RefreshTokenQueryHandler;
import com.fit.se.application.token.revoke.RevokeAllCommand;
import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.signup.SignUpUseCase;
import com.fit.se.domain.session.SessionState;
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
    private final RefreshTokenQueryHandler refreshTokenHandler;

    private final SignUpUseCase ucSignUp;
    private final SignInUseCase ucSignIn;
    private final SignOutUseCase ucSignOut;
    private final ForgotPasswordUseCase ucForgotPassword;
    private final ChangePasswordUseCase ucChangePassword;
    private final GetSessionUseCase ucGetActiveSession;

    private final LockAccountUseCase ucLockAccount;

    /*================================================================================================================*/
    @PostMapping("/signup/request")
    public ApiResponse<?> requestSignUp(@RequestBody SignUp request) {
        ucSignUp.request(request.phone());
        return ApiResponse.builder()
                .status(201)
                .message("Vui lòng xác thực OTP qua số điện thoại")
                .build();
    }


    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequest request) {
        ucSignUp.execute(request.phone(), request.password(), request.otp());
        return ApiResponse.builder()
                .status(200)
                .message("Đăng ký thành công")
                .build();
    }

    /*================================================================================================================*/

    @PostMapping("/signin")
    public ApiResponse<?> signIn(@RequestBody SignInRequest request,
                                 HttpServletRequest httpRequest) {
        GenerateTokenResult result = ucSignIn.execute(request.phone(), request.password(), httpRequest);
        return ApiResponse.builder()
                .status(200)
                .data(result)
                .message("Đăng nhập thành công")
                .build();
    }

    /*================================================================================================================*/

    @PostMapping("/signout/all")
    public ApiResponse<?> signOutAll(@RequestBody RevokeAllCommand request) {
        ucSignOut.executeAll(request.refreshToken());
        return ApiResponse.builder()
                .status(200)
                .message("Bạn đã đăng xuất tất cả các thiết bị")
                .build();
    }

    @PostMapping("/signout/{sessId}")
    public ApiResponse<?> signOutClient(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable String sessId) {
        ucSignOut.executeClient(user.getUsername(), sessId);
        return ApiResponse.builder()
                .status(200)
                .message("Bạn đã đăng xuất thiết bị")
                .build();
    }

    @PostMapping("/signout")
    public ApiResponse<?> signOut(@RequestBody RevokeRequest request,
                                  HttpServletRequest httpRequest,
                                  @AuthenticationPrincipal UserDetails user) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        ucSignOut.execute(user.getUsername(), request.refreshToken(), ip, request.sessId());
        return ApiResponse.builder()
                .status(201)
                .message("Bạn đã đăng xuất thiết bị này")
                .build();
    }

    /*================================================================================================================*/

    @PostMapping("/qr/challenges/request")
    public ApiResponse<?> requestQRChallenges(HttpServletRequest httpRequest) {
        return ApiResponse.builder()
                .status(201)
                .data(ucSignIn.generateQR(httpRequest))
                .message("Đã tạo mã QR")
                .build();
    }

    @PostMapping("/qr/challenges/{id}/approve")
    public ApiResponse<?> approveChallenge(@PathVariable("id") String id,
                                           @AuthenticationPrincipal UserDetails user) {
        ucSignIn.approveQR(id, user.getUsername());
        return ApiResponse.builder()
                .status(201)
                .message("Đăng nhập bằng mã QR")
                .build();
    }

    @PostMapping("/qr/challenges/{id}/exchange")
    public ApiResponse<?> exchangeChallenge(@PathVariable("id") String id,
                                            HttpServletRequest httpRequest) {
        return ApiResponse.builder()
                .status(201)
                .data(ucSignIn.exchangeQR(id, httpRequest))
                .message("Đăng nhập thành công")
                .build();
    }

    /*================================================================================================================*/

    @PostMapping("/token/refresh")
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
                .message("Đã làm mới token")
                .build();
    }


    /*================================================================================================================*/

    @PostMapping("/password/reset/request")
    public ApiResponse<?> requestResetPassword(@RequestBody ResetPasswordRequest request) {
        ucForgotPassword.request(request.source(), request.type());
        return ApiResponse.builder()
                .status(200)
                .message("Vui lòng xác nhận thay đổi")
                .build();
    }

    @PostMapping("/password/reset/confirm")
    public ApiResponse<?> confirmResetPassword(@RequestBody ConfirmResetPassword request) {
        String resetToken = ucForgotPassword.confirm(request.source(), request.type(), request.otp());
        return ApiResponse.builder()
                .status(200)
                .data(Map.of("resetToken", resetToken))
                .message("Xác nhận mã OTP thành công")
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
                .message("Mật khẩu đã được làm mới")
                .build();
    }

    @PostMapping("/password/change")
    public ApiResponse<?> changePassword(@AuthenticationPrincipal UserDetails user, @RequestBody ChangePasswordRequest request) {
        ucChangePassword.execute(user.getUsername(), request.newPass(), request.oldPass());
        ucSignOut.executeAll(request.refreshToken());
        return ApiResponse.builder()
                .status(200)
                .message("Thay đổi mật khẩu mới thành công")
                .build();
    }

    /*================================================================================================================*/

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

    /*================================================================================================================*/

    @PostMapping("/lock")
    public ApiResponse<?> lockAccount(@AuthenticationPrincipal UserDetails user) {
        ucLockAccount.execute(user.getUsername());
        return ApiResponse.builder().build();
    }

//    @DeleteMapping
//    public ApiResponse<?> deleteAccount(@AuthenticationPrincipal UserDetails user,) {
//        ucLockAccount.execute(user.getUsername());
//        ucSignOut.executeAll();
//        return ApiResponse.builder()
//                .build();
//    }
}
