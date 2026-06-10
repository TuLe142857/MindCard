package vn.edu.ptithcm.mindcard.dto.response.deck;

import java.time.Instant;

import lombok.Builder;
import vn.edu.ptithcm.mindcard.entity.Deck;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
public record DeckSummaryResponse(
        @Schema(description = "Unique ID of the deck")
        Integer id,
        @Schema(description = "Name of the deck")
        String name,
        @Schema(description = "Username of the owner/creator")
        String owner,
        @Schema(description = "Topic name")
        String topic,
        @Schema(description = "Visibility status")
        Deck.DeckVisibility visibility,
        @Schema(description = "Description of the deck", nullable = true)
        String description,
        @Schema(description = "Total number of cards in the deck")
        int totalCard,
        @Schema(description = "Number of users who saved this deck")
        int savedCount,
        @Schema(description = "Total number of ratings received")
        int ratingCount,
        @Schema(description = "Average rating score")
        double avgRating,
        @Schema(description = "Creation timestamp")
        Instant createdAt,
        @Schema(description = "Indicates if the authenticated user has saved this deck")
        Boolean isSaved,
        @Schema(description = "The rating given by the authenticated user (null if not rated)")
        Integer userRating
        ) {

}
