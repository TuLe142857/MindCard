package vn.edu.ptithcm.mindcard.dto.request.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterCompleteRequest(
        @Email
        @NotNull
        @NotBlank
        @Schema(description = "User's email address")
        String email,

        @NotNull
        @NotBlank
        @Schema(description = "Unique username for the new account")
        String username,

        @NotNull
        @NotBlank
        @Schema(description = "Raw password for the new account", format = "password")
        String password,

        @NotNull
        @NotBlank
        @Schema(description = "The 6-digit OTP code sent to the email")
        String otp
){}