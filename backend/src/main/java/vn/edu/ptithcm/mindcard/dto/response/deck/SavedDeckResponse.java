package vn.edu.ptithcm.mindcard.dto.response.deck;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SavedDeckResponse(
        @Schema(description = "ID of the SavedDeck (join table record)")
        Integer id,

        @Schema(description = "ID of the original Deck")
        Integer saveFrom,

        @Schema(description = "Name of the deck")
        String name,

        @Schema(description = "Username of the original creator of the deck")
        String creator,

        @Schema(description = "Topic name of the deck")
        String topic,

        @Schema(description = "Description of the deck", nullable = true)
        String description,

        @Schema(description = "Total number of cards in the deck", nullable = true)
        Integer totalCards,
        
        @Schema(description = "Number of cards in NEW status", nullable = true)
        Integer newCards,
        
        @Schema(description = "Number of cards in LEARNING status", nullable = true)
        Integer learningCards,
        
        @Schema(description = "Number of cards in REVIEW status", nullable = true)
        Integer reviewCards,

        @Schema(description = "Number of due cards (nextReviewDate <= now)", nullable = true)
        Integer dueCards,

        @Schema(description = "Indicates if the deck has updates from the creator")
        Boolean hasUpdate
) { }
