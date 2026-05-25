package vn.edu.ptithcm.mindcard.dto.response.card;

import lombok.Builder;
import vn.edu.ptithcm.mindcard.entity.Card;

@Builder
public record CardDiffResponse(
    ChangeType changeType,
    int currentVersion,
    int upcomingVersion,

    Card.CardType currentType,
    Card.CardType upcomingType,
    CardContentResponse currentFront,
    CardContentResponse upcomingFront,
    CardContentResponse currentBack,
    CardContentResponse upcomingBack
) {
    static enum ChangeType{
        NEW,
        DELETED,
        UPDATED
    }
}
