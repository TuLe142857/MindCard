package vn.edu.ptithcm.mindcard.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank
        String identity,

        @NotBlank
        String newPassword,

        @NotBlank
        String otp
) {
}
