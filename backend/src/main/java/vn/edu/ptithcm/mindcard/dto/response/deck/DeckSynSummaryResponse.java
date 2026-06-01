package vn.edu.ptithcm.mindcard.dto.response.deck;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Synchronization summary for a saved deck")
public record DeckSynSummaryResponse(
    @Schema(description = "ID of the original deck")
    int deckId,

    @Schema(description = "Total number of new cards added by creator that are not yet saved in user's progress")
    int totalNewCards,

    @Schema(description = "Total number of cards that have been updated by creator and have newer versions")
    int totalUpdatedCards,

    @Schema(description = "Total number of cards that have been deleted by creator but still exist in user's progress")
    int totalDeletedCards
) {
}
