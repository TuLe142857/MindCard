package vn.edu.ptithcm.mindcard.dto.response.deck;

import lombok.Builder;

@Builder
public record DeckSynSummaryResponse(
    int deckId,
    int totalNewCards,
    int totalUpdatedCards,
    int totalDeletedCards
) {
}
