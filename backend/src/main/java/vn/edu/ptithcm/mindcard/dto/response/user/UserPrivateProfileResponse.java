package vn.edu.ptithcm.mindcard.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response DTO for the authenticated user's own profile (private).
 * Contains sensitive information such as email, only accessible by the user themselves.
 */
@Builder
@Schema(description = "Private profile of the currently authenticated user")
public record UserPrivateProfileResponse(
        @Schema(description = "The username of the user")
        String username,

        @Schema(description = "The email address of the user")
        String email,

        @Schema(description = "The presigned URL for the user's avatar image, if available")
        String avatarUrl
) { }
