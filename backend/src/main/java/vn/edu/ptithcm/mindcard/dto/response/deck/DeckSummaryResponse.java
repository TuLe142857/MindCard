package vn.edu.ptithcm.mindcard.dto.response.deck;

import lombok.Builder;

@Builder
public record DeckSummaryResponse(
        Integer id,
        String name,
        String owner,
        String topic,
        String description,
        int totalCard,
        int savedCount,
        int ratingCount,
        double avgRating
)
{ }
