package vn.edu.ptithcm.mindcard.dto.response.deck;

import java.time.Instant;

import lombok.Builder;
import vn.edu.ptithcm.mindcard.entity.Deck;

@Builder
public record DeckSummaryResponse(
        Integer id,
        String name,
        String owner,
        String topic,
        Deck.DeckVisibility visibility,
        String description,
        int totalCard,
        int savedCount,
        int ratingCount,
        double avgRating,
        Instant createdAt
        ) {

}
