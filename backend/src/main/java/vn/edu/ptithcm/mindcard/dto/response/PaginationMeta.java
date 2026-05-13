package vn.edu.ptithcm.mindcard.dto.response;

import lombok.Getter;
import lombok.Builder;

@Builder
@Getter
public class PaginationMeta {
    private final int currentPage;
    private final int pageSize;

    private final long totalItems;
    private final long totalPages;

    private final boolean hasNext;
    private final boolean hasPrev;
}