package vn.edu.ptithcm.mindcard.dto.request.deck;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DeckRatingRequest(
        @NotNull
        @Min(value = 1)
        @Max(value = 5)
        @Schema(description = "Rating score from 1 to 5", minimum = "1", maximum = "5")
        int rating
        ) {

}
