package vn.edu.ptithcm.mindcard.dto.response.card;

import lombok.Builder;

@Builder
public record CardContentResponse(
        String text,
        String imageUrl,
        String audioUrl
) { }
