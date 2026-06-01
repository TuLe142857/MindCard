package vn.edu.ptithcm.mindcard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptithcm.mindcard.annotation.ApiError;
import vn.edu.ptithcm.mindcard.annotation.ApiErrors;
import vn.edu.ptithcm.mindcard.dto.request.common.SingleImageFileUploadRequest;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
import vn.edu.ptithcm.mindcard.dto.response.user.UserPublicProfileResponse;
import vn.edu.ptithcm.mindcard.dto.response.user.UserPrivateProfileResponse;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.security.UserPrincipal;
import vn.edu.ptithcm.mindcard.service.UserService;
import vn.edu.ptithcm.mindcard.service.DeckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final DeckService deckService;

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

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @Operation(summary = "Get current user's decks (both public and private)")
    public ResponseEntity<APIResponse.Paginated<DeckSummaryResponse>> getSelfDecks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<DeckSummaryResponse> response = deckService.getUserDecks(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(APIResponse.paginated(response));
    }

    @GetMapping("/me/saved-decks")
    public ResponseEntity<APIResponse.Success<?>> getSavedDeck() {
        return ResponseEntity.ok(APIResponse.success());
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<DeckSummaryResponse> response = deckService.getPublicDecksByUsername(username, pageable);
        return ResponseEntity.ok(APIResponse.paginated(response));
    }
}
