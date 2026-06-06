package vn.edu.ptithcm.mindcard.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LogoutRequest(
        @Schema(description = "The active access token to be invalidated", nullable = true)
        String accessToken,

        @Schema(description = "The active refresh token to be invalidated", nullable = true)
        String refreshToken
) {
}
