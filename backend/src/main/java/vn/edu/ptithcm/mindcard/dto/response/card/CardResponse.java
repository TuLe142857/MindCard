package vn.edu.ptithcm.mindcard.dto.response.card;

import lombok.Builder;
import vn.edu.ptithcm.mindcard.entity.Card;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
public record CardResponse(
        @Schema(description = "Unique ID of the card")
        int id,

        @Schema(description = "Type of the card")
        Card.CardType type,

        @Schema(description = "Front side content details")
        CardContentResponse front,

        @Schema(description = "Back side content details")
        CardContentResponse back
) { }
