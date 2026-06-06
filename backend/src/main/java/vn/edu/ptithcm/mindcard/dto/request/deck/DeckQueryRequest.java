package vn.edu.ptithcm.mindcard.dto.request.deck;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import vn.edu.ptithcm.mindcard.entity.Deck;
import vn.edu.ptithcm.mindcard.utils.PaginationUtils;
import vn.edu.ptithcm.mindcard.validation.ValidSort;

public record DeckQueryRequest(
        @Schema(description = "Search keyword for deck name", nullable = true)
        String keyword,

        @Schema(description = "Filter by topic ID", nullable = true)
        Integer topicId,

        @Schema(description = "Filter by visibility", nullable = true)
        Deck.DeckVisibility visibility,

        @ValidSort(allowedFields = {"name", "createdAt", "savedCount", "avgRating"})
        @Schema(description = "List of fields to sort by (e.g., name:asc, createdAt:desc)")
        List<String> sortBy,

        @Min(value = 1, message = "Page must be greater than 0")
        @Schema(description = "Page number (1-based)", nullable = true, minimum = "1")
        Integer page,

        @Min(value = 1, message = "Limit must be greater than 0")
        @Max(value = 100, message = "Limit cannot exceed 100")
        @Schema(description = "Number of items per page", nullable = true, minimum = "1", maximum = "100")
        Integer limit) {

    public DeckQueryRequest {
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
