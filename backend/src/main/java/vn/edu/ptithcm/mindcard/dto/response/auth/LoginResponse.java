package vn.edu.ptithcm.mindcard.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "Short-lived JWT access token")
        String accessToken,

        @Schema(description = "Long-lived JWT refresh token")
        String refreshToken
) {
}
