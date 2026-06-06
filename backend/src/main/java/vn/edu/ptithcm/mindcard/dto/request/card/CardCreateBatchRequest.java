package vn.edu.ptithcm.mindcard.dto.request.card;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CardCreateBatchRequest(
        @Schema(description = "List of cards to create in batch")
        List<CardCreateRequest> cards
) {}
