package vn.edu.ptithcm.mindcard.dto.request.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.entity.Card;

public record CardCreateRequest(
        @NotNull
        @Schema(description = "Type of the card (e.g., BASIC, CLOZE)", nullable = false)
        Card.CardType type,


        @Schema(description = "Front side text content", nullable = true)
        String frontText,

        @Schema(description = "Front side image file upload", nullable = true)
        MultipartFile frontImage,

        @Schema(description = "Front side audio file upload", nullable = true)
        MultipartFile frontAudio,


        @Schema(description = "Back side text content", nullable = true)
        String backText,

        @Schema(description = "Back side image file upload", nullable = true)
        MultipartFile backImage,

        @Schema(description = "Back side audio file upload", nullable = true)
        MultipartFile backAudio
) {}
