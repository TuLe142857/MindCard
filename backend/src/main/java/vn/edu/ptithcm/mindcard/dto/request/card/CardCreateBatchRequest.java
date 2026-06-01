package vn.edu.ptithcm.mindcard.dto.request.card;

import java.util.List;

public record CardCreateBatchRequest(
        List<CardCreateRequest> cards
) {}
