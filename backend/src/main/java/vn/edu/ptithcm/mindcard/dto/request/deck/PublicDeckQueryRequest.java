package vn.edu.ptithcm.mindcard.dto.request.deck;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import vn.edu.ptithcm.mindcard.utils.PaginationUtils;
import vn.edu.ptithcm.mindcard.validation.ValidSort;

@Builder
public record PublicDeckQueryRequest(
        @Schema(description = "Search keyword for deck name", nullable = true)
        String keyword,

        @Schema(description = "Filter by topic ID", nullable = true)
        Integer topicId,

        @ValidSort(allowedFields = {"name", "createdAt", "savedCount", "avgRating"})
        @Schema(description = "List of fields to sort by")
        List<String> sortBy,

        @Min(value = 1, message = "Page must be greater than 0")
        @Schema(description = "Page number (1-based)", minimum = "1")
        Integer page,

        @Min(value = 1, message = "Limit must be greater than 0")
        @Max(value = 100, message = "Limit cannot exceed 100")
        @Schema(description = "Number of items per page", minimum = "1", maximum = "100")
        Integer limit
        ) {

    public PublicDeckQueryRequest {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = List.of("name");
        }
    }

    public Pageable toPageable() {
        Sort sort = PaginationUtils.parseSort(this.sortBy());
        return PageRequest.of(this.page() - 1, this.limit(), sort);
    }
}
