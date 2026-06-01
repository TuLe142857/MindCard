package vn.edu.ptithcm.mindcard.dto.request.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CardReviewRequest (
        @NotNull
        @Min(0)
        @Max(5)
        int quality
){
}
