package vn.edu.ptithcm.mindcard.dto.response.common;

import lombok.Builder;
import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
public record PaginationMeta(
        @Schema(description = "Current page number (1-based)")
        int currentPage,

        @Schema(description = "Number of items per page")
        int pageSize,

        @Schema(description = "Total number of items across all pages")
        long totalItems,

        @Schema(description = "Total number of pages")
        int totalPages,

        @Schema(description = "Indicates if there is a next page")
        boolean hasNext,

        @Schema(description = "Indicates if there is a previous page")
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