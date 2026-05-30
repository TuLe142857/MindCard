package vn.edu.ptithcm.mindcard.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response DTO for a user's public profile.
 * Does NOT contain sensitive information such as email.
 */
@Builder
@Schema(description = "Public profile of a user, visible to anyone")
public record UserPublicProfileResponse(
        @Schema(description = "The username of the user")
        String username,

        @Schema(description = "The presigned URL for the user's avatar image, if available")
        String avatarUrl
) { }
