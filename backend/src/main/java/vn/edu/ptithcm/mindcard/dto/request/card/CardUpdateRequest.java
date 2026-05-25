package vn.edu.ptithcm.mindcard.dto.request.card;

import jakarta.validation.constraints.NotNull;
import vn.edu.ptithcm.mindcard.entity.Card;

public record CardUpdateRequest(
        @NotNull
        Card.CardType type,
        String frontText,
        String backText
) {
}
