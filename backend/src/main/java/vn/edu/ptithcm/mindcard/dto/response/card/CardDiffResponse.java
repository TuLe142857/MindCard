package vn.edu.ptithcm.mindcard.dto.response.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Details of difference for a card between current pinned version and creator's latest version")
public record CardDiffResponse(
        @Schema(description = "ID of the card")
        int cardId,

        @Schema(description = "Type of change detected")
        ChangeType changeType,

        @Schema(description = "Current version of the card. Can be null if change type is NEW", nullable = true)
        int currentVersion,

        @Schema(description = "Upcoming version of the card. Can be null if change type is DELETED", nullable = true)
        int upcomingVersion,

        @Schema(description = "Diff for card type", nullable = true)
        FieldDiff type,

        // -------------------------------------
        //      FRONT
        // --------------------------------------
        @Schema(description = "Diff for front side text", nullable = true)
        FieldDiff frontText,

        @Schema(description = "Diff for front side image URL", nullable = true)
        FieldDiff frontImage,

        @Schema(description = "Diff for front side audio URL", nullable = true)
        FieldDiff frontAudio,

        // -------------------------------------
        //      BACK
        // --------------------------------------

        @Schema(description = "Diff for back side text", nullable = true)
        FieldDiff backText,

        @Schema(description = "Diff for back side image URL", nullable = true)
        FieldDiff backImage,

        @Schema(description = "Diff for back side audio URL", nullable = true)
        FieldDiff backAudio
        )
{

    @Schema(description = "Represents diff values of a field")
    public record FieldDiff(
            @Schema(description = "Current value on user's device, null if empty", nullable = true)
            String current,
            @Schema(description = "Upcoming value from creator, null if empty", nullable = true)
            String upcoming
            ) {
    }

    @Schema(description = "Type of changes")
    public enum ChangeType {
        NEW,
        DELETED,
        UPDATED
    }
}
