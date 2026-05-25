package vn.edu.ptithcm.mindcard.dto.response.deck;

import lombok.Builder;

@Builder
public record SavedDeckResponse(
    Integer saveFrom,
    String username,
    String name
) { }
