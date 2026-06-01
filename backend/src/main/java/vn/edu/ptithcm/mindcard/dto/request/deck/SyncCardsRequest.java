package vn.edu.ptithcm.mindcard.dto.request.deck;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Request to sync specific cards in a saved deck")
public record SyncCardsRequest(
        @NotEmpty(message = "Card IDs list cannot be empty")
        @Schema(description = "List of card IDs to sync")
        List<Integer> cardIds
) {
}
