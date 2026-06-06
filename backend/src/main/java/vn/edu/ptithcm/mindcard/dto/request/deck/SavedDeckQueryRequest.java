package vn.edu.ptithcm.mindcard.dto.request.deck;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import vn.edu.ptithcm.mindcard.utils.PaginationUtils;
import vn.edu.ptithcm.mindcard.validation.ValidSort;

public record SavedDeckQueryRequest(
        @Schema(description = "Search keyword for saved deck name", nullable = true)
        String keyword,

        @ValidSort(allowedFields = {"savedAt"})
        @Schema(description = "List of fields to sort by")
        List<String> sortBy,

        @Schema(description = "Page number (1-based)", defaultValue = "1", minimum = "1")
        @Min(value = 1, message = "Page must be greater than 0")
        Integer page,

        @Schema(description = "Number of items per page", defaultValue = "10", minimum = "1", maximum = "100")
        @Min(value = 1, message = "Limit must be greater than 0")
        @Max(value = 100, message = "Limit cannot exceed 100")
        Integer limit
) {

    public SavedDeckQueryRequest {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }
        if (sortBy == null || sortBy().isEmpty()) {
            sortBy = List.of("savedAt");
        }
    }

    public Pageable toPageable() {
        Sort sort = PaginationUtils.parseSort(this.sortBy());
        return PageRequest.of(this.page - 1, this.limit, sort);
    }
}
