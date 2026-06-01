package vn.edu.ptithcm.mindcard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptithcm.mindcard.annotation.ApiError;
import vn.edu.ptithcm.mindcard.annotation.ApiErrors;
import vn.edu.ptithcm.mindcard.config.properties.JWTProperties;
import vn.edu.ptithcm.mindcard.dto.request.auth.*;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.auth.LoginResponse;
import vn.edu.ptithcm.mindcard.dto.response.auth.RefreshResponse;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.security.JwtService;
import vn.edu.ptithcm.mindcard.service.AuthService;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    @Autowired
    JWTProperties jwtProperties;

    @PostMapping("/register/request")
    @Operation(summary = "Step 1/2 of registration")
    @ApiErrors({
            @ApiError(value = ErrorCode.RESOURCE_ALREADY_EXIST, description = "Email already exist"),
            @ApiError(value = ErrorCode.SERVER_ERROR,  description = "Can not send email")
    })
    public ResponseEntity<APIResponse.Success<?>> requestOTPRegistration(
            @Valid @RequestBody RegisterOtpRequest body
    ){
        authService.requestOtpForRegistration(body);
        return ResponseEntity.ok(APIResponse.success(null, "An OTP code was send to your email"));
    }

    @PostMapping("/register/complete")
    @Operation(summary = "Step 2/2 of registration")
    @ApiErrors({
            @ApiError(value = ErrorCode.RESOURCE_ALREADY_EXIST, description = "Email or username already exist"),
            @ApiError(value = ErrorCode.INVALID_OTP, description = "Invalid OTP(not match or expired)")
    })
    public ResponseEntity<APIResponse.Success<?>> completeRegistration(
            @Valid @RequestBody RegisterCompleteRequest body
    ){
        authService.completeRegistration(body);
        return ResponseEntity.ok(APIResponse.success(body, "Success"));
    }


    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login and write access/refresh token to Cookies(Also return them in response body")
    @ApiError(value = ErrorCode.LOGIN_FAILED, description = "identity or password mismatch")
    public ResponseEntity<APIResponse.Success<LoginResponse>> login(
            @RequestBody LoginRequest body
    ){
        var token = authService.login(body);

        ResponseCookie accessCookie = ResponseCookie.from(jwtProperties.accessTokenCookieName(), token.accessToken())
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(jwtProperties.refreshTokenCookieName(), token.refreshToken())
                .maxAge(jwtProperties.refreshTokenExpirationSecond())
                .httpOnly(true)
                .path("/api/auth/refresh")
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(APIResponse.success(token));

    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout and add jwt token(access/refresh) to blacklist (if token not expired).")
    public ResponseEntity<APIResponse.Success<?>> logout(
        @RequestBody(required = false)
        LogoutRequest body,
        HttpServletRequest request
    ){
        String accessToken = (body != null && body.accessToken() != null)
                ? body.accessToken()
                : jwtService.extractAccessTokenFromRequest(request);

        String refreshToken = (body != null && body.refreshToken() != null)
                ? body.refreshToken()
                : jwtService.extractRefreshTokenFromRequest(request);

        authService.logout(accessToken, refreshToken);

        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/refresh")
    @Operation(summary = "refresh access token")
    @ApiErrors({
            @ApiError(ErrorCode.JWT_TOKEN_EXPIRED),
            @ApiError(ErrorCode.JWT_TOKEN_REVOKED),
            @ApiError(ErrorCode.INVALID_JWT_TOKEN)
    })
    public ResponseEntity<APIResponse.Success<RefreshResponse>> refreshAccessToken(
            @RequestBody(required = false)
            RefreshRequest body,
            HttpServletRequest request
    ){
        String refreshToken = (body != null && body.refreshToken() != null)
                ? body.refreshToken()
                : jwtService.extractRefreshTokenFromRequest(request);
        if (refreshToken == null){
            throw new AppException(ErrorCode.INVALID_JWT_TOKEN, "No token provided");
        }
        RefreshResponse res = authService.refreshAccessToken(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from(jwtProperties.accessTokenCookieName(), res.accessToken())
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(APIResponse.success(res))
                ;
    }

    @PostMapping("/forgot_password")
    @Operation(summary = "forgot password, request send otp")
    @ApiError(ErrorCode.USER_NOT_FOUND)
    public ResponseEntity<APIResponse.Success<?>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest body
    ){
        authService.forgotPassword(body.identity());
        return ResponseEntity.ok(APIResponse.success(null, "An OTP was send to your email"));
    }

    @PostMapping("/reset_password")
    @Operation(summary = "Reset Password(using OTP)")
    @ApiErrors({
            @ApiError(ErrorCode.USER_NOT_FOUND),
            @ApiError(ErrorCode.INVALID_OTP)
    })
    public ResponseEntity<APIResponse.Success<?>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest body
    ){
        authService.resetPassword(body);
        return ResponseEntity.ok(APIResponse.success());
    }
}
