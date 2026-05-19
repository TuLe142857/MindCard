package vn.edu.ptithcm.mindcard.dto.response.deck;

import lombok.Builder;

@Builder
public record DeckSummaryResponse(
        String name,
        String owner,
        String topic,
        String description,
        int totalCard
)
{ }
