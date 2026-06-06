package vn.edu.ptithcm.mindcard.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.edu.ptithcm.mindcard.annotation.ApiError;
import vn.edu.ptithcm.mindcard.annotation.ApiErrors;
import vn.edu.ptithcm.mindcard.dto.request.common.SingleImageFileUploadRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckQueryRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.PublicDeckQueryRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.SavedDeckQueryRequest;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.SavedDeckResponse;
import vn.edu.ptithcm.mindcard.dto.response.user.UserPrivateProfileResponse;
import vn.edu.ptithcm.mindcard.dto.response.user.UserPublicProfileResponse;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.security.UserPrincipal;
import vn.edu.ptithcm.mindcard.service.DeckService;
import vn.edu.ptithcm.mindcard.service.SavedDeckService;
import vn.edu.ptithcm.mindcard.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final DeckService deckService;
    private final SavedDeckService savedDeckService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's private profile (includes email)")
    @ApiError(value = ErrorCode.USER_NOT_FOUND, description = "User not found")
    public ResponseEntity<APIResponse.Success<UserPrivateProfileResponse>> getSelfProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String username = userPrincipal.getUsername();
        UserPrivateProfileResponse response = userService.getSelfProfile(username);
        return ResponseEntity.ok(APIResponse.success(response));
    }

    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user avatar, return new avatar url if success")
    @ApiErrors({
        @ApiError(value = ErrorCode.USER_NOT_FOUND, description = "User not found"),
        @ApiError(value = ErrorCode.FILE_UPLOAD_FAILED, description = "Failed to upload avatar image")
    })
    public ResponseEntity<APIResponse.Success<String>> updateAvatar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute @Valid SingleImageFileUploadRequest body
    ) {
        int userId = userPrincipal.getId();
        String avatarUrl = userService.updateAvatar(userId, body.file());
        return ResponseEntity.ok(APIResponse.success(avatarUrl, "Avatar updated successfully"));
    }

    @GetMapping("/me/decks")
    @Operation(summary = "Get current user's decks")
    public ResponseEntity<APIResponse.Paginated<DeckSummaryResponse>> getSelfDecks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute @Valid DeckQueryRequest query
    ) {

        Page<DeckSummaryResponse> response = deckService.getUserDecks(userPrincipal.getId(), query);
        return ResponseEntity.ok(APIResponse.paginated(response));
    }

    @GetMapping("/me/saved-decks")
    @Operation(summary = "Get current user's saved decks")
    public ResponseEntity<APIResponse.Paginated<SavedDeckResponse>> getSavedDeck(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute @Valid SavedDeckQueryRequest query
    ) {
        int userId = userPrincipal.getId();
        Page<SavedDeckResponse> res = savedDeckService.listSavedDecks(userId, query);
        return ResponseEntity.ok(APIResponse.paginated(res));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get a user's public profile by username")
    @ApiError(value = ErrorCode.USER_NOT_FOUND, description = "User not found")
    public ResponseEntity<APIResponse.Success<UserPublicProfileResponse>> getUserProfile(
            @PathVariable String username
    ) {
        UserPublicProfileResponse response = userService.getPublicProfile(username);
        return ResponseEntity.ok(APIResponse.success(response));
    }

    @GetMapping("/{username}/decks")
    @Operation(summary = "Get a user's public decks by username")
    @ApiError(value = ErrorCode.USER_NOT_FOUND, description = "User not found")
    public ResponseEntity<APIResponse.Paginated<DeckSummaryResponse>> getUserDecks(
            @PathVariable String username,
            @ModelAttribute @Valid PublicDeckQueryRequest query
    ) {
        Page<DeckSummaryResponse> response = deckService.getPublicDecksByUsername(username, query);
        return ResponseEntity.ok(APIResponse.paginated(response));
    }
}
