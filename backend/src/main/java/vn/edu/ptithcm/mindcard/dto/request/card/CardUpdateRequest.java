package vn.edu.ptithcm.mindcard.dto.request.card;

import vn.edu.ptithcm.mindcard.entity.Card;

public record CardUpdateRequest(
        Card.CardType type,
        String frontText,
        String backText
) {
}
