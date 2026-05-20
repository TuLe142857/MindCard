package vn.edu.ptithcm.mindcard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;

@RestController
@RequestMapping("/api/saved-decks")
@Tag(name = "Saved Deck")
public class SavedDeckController {
    @GetMapping("")
    @Operation(summary = "Get list of saved decks")
    public ResponseEntity<APIResponse.Success<?>> getSavedDecks(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/{deckId}")
    @Operation(summary = "Get Saved Deck Summary")
    public ResponseEntity<APIResponse.Success<?>> getSavedDeckSummary(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/{deckId}/update-status")
    @Operation(summary = "Check for update")
    public ResponseEntity<APIResponse.Success<?>> checkDeckUpdate(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @PatchMapping("/{deckId}/sync")
    @Operation(summary = "Sync all cards of saved deck")
    public ResponseEntity<APIResponse.Success<?>> syncAllCards(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/{deckId}/card/{cardId}/diff")
    @Operation(summary = "Check diff between 2 version of card")
    public ResponseEntity<APIResponse.Success<?>> checkCardDiff(){
        return ResponseEntity.ok(APIResponse.success());
    }


    @GetMapping("/{id}/cards/queue")
    @Operation(summary = "Get study queue")
    public ResponseEntity<APIResponse.Success<?>> getStudyQueue(
            @RequestParam(defaultValue = "10") Integer limit
    ){
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/{deckId}/cards/{cardId}/review")
    public ResponseEntity<APIResponse.Success<?>> reviewCard(){
        return ResponseEntity.ok(APIResponse.success());
    }
}
