package vn.edu.ptithcm.mindcard.dto.request.card;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.ptithcm.mindcard.entity.Card;

public record CardUpdateRequest(

        @Schema(description = "Type of the card", nullable = true)
        Card.CardType type,

        @Schema(description = "Updated front side text", nullable = true)
        String frontText,

        @Schema(description = "Updated back side text", nullable = true)
        String backText
) {
}
