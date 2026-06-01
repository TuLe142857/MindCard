package vn.edu.ptithcm.mindcard.dto.request.deck;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;


public record UpdateSavedDeckRequest(
        @Schema(description = "New name for the saved deck", nullable = true)
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Schema(description = "New description for the saved deck", nullable = true)
        String description
        ) {

}
