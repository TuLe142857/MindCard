package vn.edu.ptithcm.mindcard.dto.response.card;

import lombok.Builder;
import vn.edu.ptithcm.mindcard.entity.Card;

@Builder
public record CardResponse(
        int id,
        Card.CardType type,
        CardContentResponse front,
        CardContentResponse back
) { }
