package vn.edu.ptithcm.mindcard.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    @Autowired
    JWTProperties jwtProperties;

    @GetMapping("/whoami")
    public ResponseEntity<APIResponse<?>> whoami(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)){
            return ResponseEntity.ok(APIResponse.ok(authentication.getPrincipal()));
        }else{
            throw new AppException(ErrorCode.UNAUTHENTICATED, "user not login");
        }

    }

    @PostMapping("/register/request")
    public ResponseEntity<APIResponse<?>> requestOTPRegistration(
            @Valid @RequestBody RegisterOtpRequest body
    ){
        authService.requestOtpForRegistration(body);
        return ResponseEntity.ok(APIResponse.ok(null, "An OTP code was send to your email"));
    }

    @PostMapping("/register/complete")
    public ResponseEntity<APIResponse<?>> completeRegistration(
            @Valid @RequestBody RegisterCompleteRequest body
    ){
        authService.completeRegistration(body);
        return ResponseEntity.ok(APIResponse.ok(body, "Success"));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponse>> login(
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
                .path("/auth/refresh")
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(APIResponse.ok(token));

    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<?>> logout(
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

        return ResponseEntity.ok(APIResponse.ok());
    }

    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<RefreshResponse>> refreshAccessToken(
            @RequestBody(required = false)
            RefreshRequest body,
            HttpServletRequest request
    ){
        String refreshToken = (body != null && body.refreshToken() != null)
                ? body.refreshToken()
                : jwtService.extractRefreshTokenFromRequest(request);
        RefreshResponse res = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(APIResponse.ok(res));
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<APIResponse<?>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest body
    ){
        authService.forgotPassword(body.identity());
        return ResponseEntity.ok(APIResponse.ok(null, "An OTP was send to your email"));
    }

    @PostMapping("/reset_password")
    public ResponseEntity<APIResponse<?>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest body
    ){
        authService.resetPassword(body);
        return ResponseEntity.ok(APIResponse.ok());
    }
}
