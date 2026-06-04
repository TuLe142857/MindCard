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
        @Schema(nullable = true)
        String keyword,
        @Schema(nullable = true)
        Integer topicId,
        @Schema(nullable = true)
        Deck.DeckVisibility visibility,
        @ValidSort(allowedFields = {"name", "createdAt", "savedCount", "avgRating"})
        List<String> sortBy,
        @Min(value = 1, message = "Page must be greater than 0")
        Integer page,
        @Min(value = 1, message = "Limit must be greater than 0")
        @Max(value = 100, message = "Limit cannot exceed 100")
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
