package vn.edu.ptithcm.mindcard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

import java.util.List;

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
    public ResponseEntity<APIResponse.Success<?>> getSavedDeckSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ){
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/{savedDeckId}/sync-summary")
    @Operation(summary = "Check for update")
    public ResponseEntity<APIResponse.Success<DeckSynSummaryResponse>> checkDeckUpdate(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ){
        int userId = userPrincipal.getId();
        var res = savedDeckService.checkSyncStatus(userId, savedDeckId);
        return ResponseEntity.ok(APIResponse.success(res));
    }

    @GetMapping("/{savedDeckId}/sync-details")
    @Operation(summary = "Show list of available updated cards")
    public ResponseEntity<APIResponse.Success<List<CardDiffResponse>>> showListSyncCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ){
        int userId = userPrincipal.getId();
        var res = savedDeckService.cardsDiff(userId, savedDeckId);
        return ResponseEntity.ok(APIResponse.success(res));
    }

    @PatchMapping("/{savedDeckId}/sync")
    @Operation(summary = "Sync all cards of saved deck")
    public ResponseEntity<APIResponse.Success<?>> syncAllCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId
    ){
        int userId = userPrincipal.getId();
        savedDeckService.syncAll(userId, savedDeckId);
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/{savedDeckId}/cards/batch")
    @Operation(summary = "Get card for study/review")
    public ResponseEntity<APIResponse.Success<List<CardResponse>>> getStudyQueue(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int savedDeckId,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "review") String type
    ){
        if(! (type.equals("new") || type.equals("review"))){
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
