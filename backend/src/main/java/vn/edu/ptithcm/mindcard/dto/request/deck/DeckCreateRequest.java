package vn.edu.ptithcm.mindcard.dto.request.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.edu.ptithcm.mindcard.entity.Deck;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeckCreateRequest(

        @NotBlank
        @Schema(description = "Name of the deck")
        String name,

        @NotNull
        @Schema(description = "Visibility status (PUBLIC/PRIVATE)")
        Deck.DeckVisibility visibility,

        @NotNull
        @Schema(description = "ID of the topic this deck belongs to")
        Integer topicId,

        @Schema(description = "Optional description for the deck")
        String description
) {}

