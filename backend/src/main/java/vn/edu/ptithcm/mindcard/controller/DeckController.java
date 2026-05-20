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
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
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
    public ResponseEntity<APIResponse.Success<?>> searchDeck(){
        return ResponseEntity.ok(APIResponse.success());
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

    @PostMapping("/{deckId}/save")
    @Operation(summary = "Save deck")
    public ResponseEntity<APIResponse.Success<?>> saveDeck(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/{deckId}/ratings")
    @Operation(summary = "Rating Deck (1-5 stars)")
    public ResponseEntity<APIResponse.Success<?>> ratingDeck(
            @RequestBody @Valid DeckRatingRequest body
            ){
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

    @GetMapping("/{deckId}/card/queue")
    @Operation(summary = "Get study queue")
    public ResponseEntity<APIResponse.Success<?>> getStudyQueue(
            @RequestParam(defaultValue = "10") Integer limit
    ){
        return ResponseEntity.ok(APIResponse.success());
    }
}
