package vn.edu.ptithcm.mindcard.dto.request.deck;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DeckRatingRequest(
        @Min(value = 1)
        @Max(value = 5)
        int rating
) {
}
