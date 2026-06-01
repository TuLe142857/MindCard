package vn.edu.ptithcm.mindcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.edu.ptithcm.mindcard.annotation.ApiError;
import vn.edu.ptithcm.mindcard.annotation.ApiErrors;
import vn.edu.ptithcm.mindcard.dto.response.card.CardDiffResponse;
import vn.edu.ptithcm.mindcard.dto.response.card.CardResponse;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSynSummaryResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.SavedDeckResponse;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.security.UserPrincipal;
import vn.edu.ptithcm.mindcard.service.SavedDeckService;
import vn.edu.ptithcm.mindcard.service.StudyService;

@RestController
@RequestMapping("/api/saved-decks")
@Tag(name = "Saved Deck")
public class SavedDeckController {

    @Autowired
    private SavedDeckService savedDeckService;

    @Autowired
    private StudyService studyService;

    @GetMapping("/{savedDeckId}")
    @Operation(summary = "Get Saved Deck Summary")
    @ApiErrors({
        @ApiError(value = ErrorCode.RESOURCE_NOT_FOUND, description = "Saved deck not found"),
        @ApiError(value = ErrorCode.FORBIDDEN, description = "Saved deck does not belong to the user")
    })
    public ResponseEntity<APIResponse.Success<SavedDeckResponse>> getSavedDeckSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ) {
        SavedDeckResponse response = savedDeckService.getSavedDeckSummary(userPrincipal.getId(), savedDeckId);
        return ResponseEntity.ok(APIResponse.success(response));
    }

    @GetMapping("/{savedDeckId}/sync-summary")
    @Operation(
            summary = "Check for update",
            description = "Checks the synchronization status of a saved deck, showing the count of new, updated, and deleted cards."
    )
    @ApiErrors({
        @ApiError(value = ErrorCode.RESOURCE_NOT_FOUND, description = "Saved deck not found"),
        @ApiError(value = ErrorCode.FORBIDDEN, description = "Saved deck does not belong to the requesting user")
    })
    public ResponseEntity<APIResponse.Success<DeckSynSummaryResponse>> checkDeckUpdate(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ) {
        int userId = userPrincipal.getId();
        var res = savedDeckService.checkSyncStatus(userId, savedDeckId);
        return ResponseEntity.ok(APIResponse.success(res));
    }

    @GetMapping("/{savedDeckId}/sync-details")
    @Operation(
            summary = "Show list of available updated cards",
            description = "Retrieves a paginated list of diffs for cards that are out of sync (new, updated, or deleted)."
    )
    @ApiErrors({
        @ApiError(value = ErrorCode.RESOURCE_NOT_FOUND, description = "Saved deck not found"),
        @ApiError(value = ErrorCode.FORBIDDEN, description = "Saved deck does not belong to the user")
    })
    public ResponseEntity<APIResponse.Paginated<CardDiffResponse>> showListSyncCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @PathVariable int savedDeckId
    ) {
        int userId = userPrincipal.getId();
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<CardDiffResponse> res = savedDeckService.cardsDiff(userId, savedDeckId, pageable);
        return ResponseEntity.ok(APIResponse.paginated(res));
    }

    @PatchMapping("/{savedDeckId}/sync")
    @Operation(
            summary = "Sync all cards of saved deck",
            description = "Synchronizes all out-of-sync cards in the saved deck with their latest versions."
    )
    @ApiErrors({
        @ApiError(value = ErrorCode.RESOURCE_NOT_FOUND, description = "Saved deck not found"),
        @ApiError(value = ErrorCode.FORBIDDEN, description = "Saved deck does not belong to the requesting user")
    })
    public ResponseEntity<APIResponse.Success<?>> syncAllCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ) {
        int userId = userPrincipal.getId();
        savedDeckService.syncAll(userId, savedDeckId);
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/{savedDeckId}/sync/partial")
    @Operation(
            summary = "Sync specific cards",
            description = "Synchronizes a specific list of out-of-sync cards in the saved deck."
    )
    @ApiErrors({
        @ApiError(value = ErrorCode.RESOURCE_NOT_FOUND, description = "Saved deck not found"),
        @ApiError(value = ErrorCode.FORBIDDEN, description = "Saved deck does not belong to the requesting user")
    })
    public ResponseEntity<APIResponse.Success<?>> syncCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId,
            @jakarta.validation.Valid @RequestBody vn.edu.ptithcm.mindcard.dto.request.deck.SyncCardsRequest request
    ) {
        int userId = userPrincipal.getId();
        savedDeckService.syncCard(userId, savedDeckId, request.cardIds());
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/{savedDeckId}/cards/batch")
    @Operation(summary = "Get card for study/review")
    public ResponseEntity<APIResponse.Success<List<CardResponse>>> getStudyQueue(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "review") String type
    ) {
        if (!(type.equals("new") || type.equals("review"))) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid type");
        }

        int userId = userPrincipal.getId();
        List<CardResponse> cards = (type.equals("new"))
                ? studyService.getNewCardsBatch(userId, savedDeckId, limit)
                : studyService.getDueCardBatch(userId, savedDeckId, limit);
        ;

        return ResponseEntity.ok(APIResponse.success(cards));
    }
}
