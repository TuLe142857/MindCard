package vn.edu.ptithcm.mindcard.dto.request.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CardReviewRequest (
        @NotNull
        @Min(0)
        @Max(5)
        @Schema(description = "Quality of the review score (0-5) representing user's recall performance", minimum = "0", maximum = "5", nullable = false)
        int quality
){
}
