package vn.edu.ptithcm.mindcard.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank
        String identity
) {
}
