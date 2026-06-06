package vn.edu.ptithcm.mindcard.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank
        @Schema(description = "Email or username of the account to reset")
        String identity,

        @NotBlank
        @Schema(description = "The new raw password", format = "password")
        String newPassword,

        @NotBlank
        @Schema(description = "The 6-digit OTP code received from email")
        String otp
) {
}
