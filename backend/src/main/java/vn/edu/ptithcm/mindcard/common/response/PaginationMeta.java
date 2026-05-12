package vn.edu.ptithcm.mindcard.common.response;

public record PaginationMeta(
        int currentPage,
        int pageSize,
        long totalItems,
        long totalPages,
        boolean hasNext,
        boolean hasPrev
) {

}
