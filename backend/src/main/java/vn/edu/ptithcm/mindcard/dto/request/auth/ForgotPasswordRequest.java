package vn.edu.ptithcm.mindcard.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank
        @Schema(description = "Email or username of the user requesting password reset")
        String identity
) {
}
