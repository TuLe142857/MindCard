package vn.edu.ptithcm.mindcard.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptithcm.mindcard.dto.request.card.CardCreateBatchRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckCreateRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckRatingRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckUpdateRequest;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import vn.edu.ptithcm.mindcard.service.AuthService;
import vn.edu.ptithcm.mindcard.service.CardService;
import vn.edu.ptithcm.mindcard.service.DeckService;

@RestController
@RequestMapping("/api/decks")
@Tag(name = "Deck")
public class DeckController {
    @Autowired
    private DeckService deckService;

    @Autowired
    private CardService cardService;

    @Autowired
    private AuthService authService;

    @PostMapping("")
    @Operation(summary = "Create deck")
    public ResponseEntity<APIResponse.Success<?>> createDeck(
            @Valid @RequestBody DeckCreateRequest body
    ){
        int userId = authService.getCurrentUserPrincipal().getId();
        deckService.createDeck(userId, body);
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("")
    @Operation(summary = "Search Public Deck")
    public ResponseEntity<APIResponse.Paginated<DeckSummaryResponse>> searchDeck(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<DeckSummaryResponse> decks = deckService.searchPublicDecks(keyword, pageable);
        return ResponseEntity.ok(APIResponse.paginated(decks));
    }

    @GetMapping("/{deckId}")
    @Operation(summary = "Get Deck by id")
    public ResponseEntity<APIResponse.Success<DeckSummaryResponse>> getDeckDetails(
            @PathVariable int deckId
    ){
        int userId = authService.getCurrentUserPrincipal().getId();
        var response = deckService.getDeckSummary(userId, deckId);
        return ResponseEntity.ok(APIResponse.success(response));
    }

    @PostMapping("/{deckId}")
    @Operation(summary = "Update Deck")
    public ResponseEntity<APIResponse.Success<?>> updateDeck(
            @RequestBody DeckUpdateRequest body,
            @PathVariable int deckId
            )
    {
        deckService.updateDeck(
                authService.getCurrentUserPrincipal().getId(),
                deckId,
                body
        );
        return ResponseEntity.ok(APIResponse.success());
    }


    @PostMapping("/{deckId}/save")
    @Operation(summary = "Save deck")
    public ResponseEntity<APIResponse.Success<?>> saveDeck(
            @PathVariable int deckId
    ){
        int userId = authService.getCurrentUserPrincipal().getId();
        deckService.saveDeck(userId, deckId);
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/{deckId}/rating")
    @Operation(summary = "Rating Deck (1-5 stars)")
    public ResponseEntity<APIResponse.Success<?>> ratingDeck(
            @RequestBody @Valid DeckRatingRequest body,
            @PathVariable int deckId
    ){
        deckService.ratingDeck(
                authService.getCurrentUserPrincipal().getId(),
                deckId,
                body.rating()
        );
        return ResponseEntity.ok(APIResponse.success());
    }


    @PostMapping(value = "/{deckId}/card/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Add cards to deck",
            description = "Request type: form data. To pass list of card: use keyword cards[index].{attribute_name} for each form value"
    )
    public ResponseEntity<APIResponse.Success<?>> addCardToDeck(
            @Valid @ModelAttribute CardCreateBatchRequest form,
            @PathVariable int deckId
    )
    {
        int userId = authService.getCurrentUserPrincipal().getId();
        cardService.createCards(userId, deckId, form.cards());
        return ResponseEntity.ok(APIResponse.success());
    }

}
