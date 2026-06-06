package vn.edu.ptithcm.mindcard.dto.request.auth;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterOtpRequest(
        @NotNull
        @NotBlank
        @Email
        @Schema(description = "Email address to receive the registration OTP")
        String email
) {
}
