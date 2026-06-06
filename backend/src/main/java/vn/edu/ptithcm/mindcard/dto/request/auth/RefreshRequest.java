package vn.edu.ptithcm.mindcard.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshRequest(
        @Schema(description = "A valid, non-expired refresh token used to get a new access token", nullable = true)
        String refreshToken
) {

}
