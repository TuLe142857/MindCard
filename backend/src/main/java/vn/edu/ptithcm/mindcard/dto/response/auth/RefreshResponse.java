package vn.edu.ptithcm.mindcard.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshResponse(
        @Schema(description = "New short-lived JWT access token")
        String accessToken
) {}
