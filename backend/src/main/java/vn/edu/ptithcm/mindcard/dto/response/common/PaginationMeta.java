package vn.edu.ptithcm.mindcard.dto.response.common;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PaginationMeta(
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrev
) {
    /**
     * Build PaginationMeta from Page object
     * @param page page object
     * @return pagination meta
     */
    public static  PaginationMeta fromPage(Page<?> page){
        return PaginationMeta.builder()
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrev(page.hasPrevious())
                .build();
    }
}