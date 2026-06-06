package vn.edu.ptithcm.mindcard.dto.response.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CardContentResponse(
        @Schema(description = "Text content of the card side")
        String text,

        @Schema(description = "URL to the image of the card side", nullable = true)
        String imageUrl,

        @Schema(description = "URL to the audio of the card side", nullable = true)
        String audioUrl
        ) {

}
