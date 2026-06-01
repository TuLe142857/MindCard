package vn.edu.ptithcm.mindcard.dto.request.card;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.entity.Card;

public record CardCreateRequest(
        @NotNull
        Card.CardType type,

        String frontText,
        MultipartFile frontImage,
        MultipartFile frontAudio,

        String backText,
        MultipartFile backImage,
        MultipartFile backAudio
) {}
