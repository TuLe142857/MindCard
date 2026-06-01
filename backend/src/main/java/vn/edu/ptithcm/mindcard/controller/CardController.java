package vn.edu.ptithcm.mindcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vn.edu.ptithcm.mindcard.dto.request.card.CardReviewRequest;
import vn.edu.ptithcm.mindcard.dto.request.card.CardUpdateRequest;
import vn.edu.ptithcm.mindcard.dto.request.common.SingleAudioFileUploadRequest;
import vn.edu.ptithcm.mindcard.dto.request.common.SingleImageFileUploadRequest;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.security.UserPrincipal;
import vn.edu.ptithcm.mindcard.service.CardService;
import vn.edu.ptithcm.mindcard.service.StudyService;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card")
public class CardController {

    @Autowired
    private StudyService studyService;

    @Autowired
    private CardService cardService;

    @DeleteMapping("/{cardId}")
    @Operation(summary = "Delete card - coming soon")
    public ResponseEntity<APIResponse.Success<?>> deleteCard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId
    ){
        return ResponseEntity.ok(APIResponse.success());
    }


    @PostMapping("/{cardId}")
    @Operation(summary = "Update card")
    public ResponseEntity<APIResponse.Success<?>> updateCard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId,
            @RequestBody @Valid CardUpdateRequest body
    ) {
        int userId = userPrincipal.getId();
        cardService.update(userId, cardId, body);
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping(value = "{cardId}/frontImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update front image")
    public ResponseEntity<APIResponse.Success<?>> updateFrontImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId,
            @ModelAttribute @Valid SingleImageFileUploadRequest body
    ) {
        int userId = userPrincipal.getId();
        cardService.updateFrontImage(userId, cardId, body.file());
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping(value = "{cardId}/frontAudio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update front audio")
    public ResponseEntity<APIResponse.Success<?>> updateFrontAudio(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId,
            @ModelAttribute @Valid SingleAudioFileUploadRequest body
    ) {
        int userId = userPrincipal.getId();
        cardService.updateFrontAudio(userId, cardId, body.file());
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping(value = "{cardId}/backImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update back image")
    public ResponseEntity<APIResponse.Success<?>> updateBackImage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId,
            @ModelAttribute @Valid SingleImageFileUploadRequest body
    ) {
        int userId = userPrincipal.getId();
        cardService.updateBackImage(userId, cardId, body.file());
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping(value = "{cardId}/backAudio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update back audio")
    public ResponseEntity<APIResponse.Success<?>> updateBackAudio(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId,
            @ModelAttribute SingleAudioFileUploadRequest body
    ) {
        int userId = userPrincipal.getId();
        cardService.updateBackAudio(userId, cardId, body.file());
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/{cardId}/review")
    @Operation(summary = "Set card study quality [0-5] to calculate next review date")
    public ResponseEntity<APIResponse.Success<?>> reviewCard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int cardId,
            @RequestBody CardReviewRequest body
    ) {
        studyService.setCardReviewQuality(userPrincipal.getId(), cardId, body.quality());
        return ResponseEntity.ok(APIResponse.success());
    }

}
