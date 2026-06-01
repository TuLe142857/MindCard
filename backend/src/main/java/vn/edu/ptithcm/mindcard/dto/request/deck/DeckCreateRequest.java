package vn.edu.ptithcm.mindcard.dto.request.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.edu.ptithcm.mindcard.entity.Deck;

public record DeckCreateRequest(

        @NotBlank
        String name,

        @NotNull
        Deck.DeckVisibility visibility,

        @NotNull
        Integer topicId,

        String description
) {}

