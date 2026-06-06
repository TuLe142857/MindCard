package vn.edu.ptithcm.mindcard.dto.request.deck;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import vn.edu.ptithcm.mindcard.entity.Deck.DeckVisibility;

public record UpdateDeckVisibilityRequest(
        @NotNull(message = "Visibility cannot be null")
        @Schema(description = "The new visibility status of the deck")
        DeckVisibility visibility
        ) {

}
