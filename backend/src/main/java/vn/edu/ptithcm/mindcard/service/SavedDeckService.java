package vn.edu.ptithcm.mindcard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.edu.ptithcm.mindcard.dto.request.deck.UpdateSavedDeckRequest;
import vn.edu.ptithcm.mindcard.dto.response.card.CardDiffResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSynSummaryResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.SavedDeckResponse;
import vn.edu.ptithcm.mindcard.entity.Card;
import vn.edu.ptithcm.mindcard.entity.CardVersion;
import vn.edu.ptithcm.mindcard.entity.Deck;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;
import vn.edu.ptithcm.mindcard.entity.UserCardProgress;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.CardRepository;
import vn.edu.ptithcm.mindcard.repository.SavedDeckRepository;
import vn.edu.ptithcm.mindcard.repository.UserCardProgressRepository;
import vn.edu.ptithcm.mindcard.repository.projection.CardSyncProjection;

@Service
public class SavedDeckService {

    @Autowired
    private SavedDeckRepository savedDeckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserCardProgressRepository userCardProgressRepository;

    @Autowired
    private StorageService storageService;

    public Page<SavedDeckResponse> listSavedDecks(int userId, Pageable pageable) {
        return savedDeckRepository.findByUserId(userId, pageable)
                .map(savedDeck -> mapToSavedDeckResponse(savedDeck, userId));
    }

    /**
     * Retrieves the summary of a specific saved deck including study progress.
     *
     * @param userId the ID of the requesting user.
     * @param savedDeckId the ID of the saved deck.
     * @return a {@link SavedDeckResponse} containing summary and progress
     * stats.
     * @throws AppException if any validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the saved deck does not
     * exist.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - if the saved deck does not belong to
     * the user.</li>
     * </ul>
     */
    public SavedDeckResponse getSavedDeckSummary(int userId, int savedDeckId) throws AppException {
        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this saved deck");
        }

        return mapToSavedDeckResponse(savedDeck, userId);
    }

    /**
     * Updates the custom name and description of a user's saved deck.
     *
     * @param userId the ID of the requesting user.
     * @param savedDeckId the ID of the saved deck to update.
     * @param request the request containing the new name and description.
     * @return a {@link SavedDeckResponse} reflecting the updated saved deck.
     * @throws AppException if any validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the saved deck does not
     * exist.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - if the saved deck does not belong to
     * the user.</li>
     * </ul>
     */
    public SavedDeckResponse updateSavedDeck(int userId, int savedDeckId, UpdateSavedDeckRequest request) throws AppException {
        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this saved deck");
        }

        if (request.name() != null) {
            savedDeck.setName(request.name());
        }
        if (request.description() != null) {
            savedDeck.setDescription(request.description());
        }

        savedDeckRepository.save(savedDeck);

        return mapToSavedDeckResponse(savedDeck, userId);
    }

    private SavedDeckResponse mapToSavedDeckResponse(SavedDeck savedDeck, int userId) {

        Deck originalDeck = savedDeck.getDeck();
        int deckId = originalDeck.getId();

        int totalCards = userCardProgressRepository.countTotalCards(userId, deckId);
        int newCards = userCardProgressRepository.countCardsByStatus(userId, deckId, UserCardProgress.CardStatus.NEW);
        int learningCards = userCardProgressRepository.countCardsByStatus(userId, deckId, UserCardProgress.CardStatus.LEARNING);
        int reviewCards = userCardProgressRepository.countCardsByStatus(userId, deckId, UserCardProgress.CardStatus.REVIEW);
        int dueCards = userCardProgressRepository.countDueCards(userId, deckId);

        boolean isOriginalDeckActive = originalDeck.getVisibility() == Deck.DeckVisibility.PUBLIC 
                                       && !originalDeck.getIsDeleted();

        boolean hasUpdate = false;
        if (isOriginalDeckActive) {
            int newCardsOutOfSync = userCardProgressRepository.countNewCards(userId, deckId);
            int updatedCardsOutOfSync = userCardProgressRepository.countUpdatedCards(userId, deckId);
            int deletedCardsOutOfSync = userCardProgressRepository.countDeletedCards(userId, deckId);
            hasUpdate = (newCardsOutOfSync > 0) || (updatedCardsOutOfSync > 0) || (deletedCardsOutOfSync > 0);
        }

        return SavedDeckResponse.builder()
                .id(savedDeck.getId())
                .originalDeckId(deckId)
                .originalDeckName(originalDeck.getName())
                .creator(savedDeck.getDeck().getOwner().getUsername())
                .name(savedDeck.getName() != null ? savedDeck.getName() : originalDeck.getName())
                .description(savedDeck.getDescription() != null ? savedDeck.getDescription() : originalDeck.getDescription())
                .topic(originalDeck.getTopic().getName())
                .totalCards(totalCards)
                .newCards(newCards)
                .learningCards(learningCards)
                .reviewCards(reviewCards)
                .dueCards(dueCards)
                .hasUpdate(hasUpdate)
                .isOriginalDeckActive(isOriginalDeckActive)
                .build();

    }

    /**
     * Checks the synchronization status between a user's saved deck progress
     * and the original deck. Computes the count of new cards, updated cards,
     * and deleted cards.
     *
     * @param userId the ID of the requesting user.
     * @param savedDeckId the ID of the saved deck.
     * @return a {@link DeckSynSummaryResponse} containing the counts of
     * changes.
     * @throws AppException if any validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the saved deck with the
     * specified savedDeckId does not exist.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - if the saved deck does not belong to
     * the user.</li>
     * </ul>
     */
    public DeckSynSummaryResponse checkSyncStatus(int userId, int savedDeckId) throws AppException {
        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this saved deck");
        }

        int deckId = savedDeck.getDeck().getId();
        int newCards = userCardProgressRepository.countNewCards(userId, deckId);
        int updatedCards = userCardProgressRepository.countUpdatedCards(userId, deckId);
        int deletedCards = userCardProgressRepository.countDeletedCards(userId, deckId);

        return DeckSynSummaryResponse.builder()
                .deckId(deckId)
                .totalNewCards(newCards)
                .totalUpdatedCards(updatedCards)
                .totalDeletedCards(deletedCards)
                .build();
    }

    /**
     * Retrieves a paginated list of card differences (diffs) between a user's
     * saved progress and the creator's original deck.
     *
     * @param userId the ID of the requesting user.
     * @param savedDeckId the ID of the saved deck.
     * @param pageable pagination and sorting information.
     * @return a page of {@link CardDiffResponse} containing differences for
     * each out-of-sync card.
     * @throws AppException if any validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the saved deck with the
     * specified savedDeckId does not exist.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - if the saved deck does not belong to
     * the user.</li>
     * </ul>
     */
    public Page<CardDiffResponse> cardsDiff(int userId, int savedDeckId, Pageable pageable) throws AppException {
        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this saved deck");
        }

        int deckId = savedDeck.getDeck().getId();
        Page<CardSyncProjection> outOfSyncPage = cardRepository.findOutOfSyncCards(userId, deckId, pageable);

        return outOfSyncPage.map(row -> {
            Card card = row.getCard();
            UserCardProgress progress = row.getProgress();

            CardVersion latest = card.getLatestVersion();
            CardVersion current = (progress != null) ? progress.getCardVersion() : null;

            boolean isDeleted = card.getIsDeleted();
            CardDiffResponse.ChangeType changeType;
            if (isDeleted) {
                changeType = CardDiffResponse.ChangeType.DELETED;
            } else if (progress == null) {
                changeType = CardDiffResponse.ChangeType.NEW;
            } else {
                changeType = CardDiffResponse.ChangeType.UPDATED;
            }

            int currentVer = (current != null) ? current.getVersion() : 0;
            int upcomingVer = (!isDeleted && latest != null) ? latest.getVersion() : 0;

            String currentType = (current != null) ? current.getType().name() : null;
            String upcomingType = (!isDeleted && latest != null) ? latest.getType().name() : null;
            CardDiffResponse.FieldDiff typeDiff = getFieldDiff(currentType, upcomingType);

            String currentFrontText = (current != null && current.getFrontContent() != null) ? current.getFrontContent().getText() : null;
            String upcomingFrontText = (!isDeleted && latest != null && latest.getFrontContent() != null) ? latest.getFrontContent().getText() : null;
            CardDiffResponse.FieldDiff frontTextDiff = getFieldDiff(currentFrontText, upcomingFrontText);

            String currentFrontImage = (current != null && current.getFrontContent() != null && current.getFrontContent().getImageKey() != null)
                    ? storageService.generatePresignedUrl(current.getFrontContent().getImageKey(), java.time.Duration.ofHours(1)) : null;
            String upcomingFrontImage = (!isDeleted && latest != null && latest.getFrontContent() != null && latest.getFrontContent().getImageKey() != null)
                    ? storageService.generatePresignedUrl(latest.getFrontContent().getImageKey(), java.time.Duration.ofHours(1)) : null;
            CardDiffResponse.FieldDiff frontImageDiff = getFieldDiff(currentFrontImage, upcomingFrontImage);

            String currentFrontAudio = (current != null && current.getFrontContent() != null && current.getFrontContent().getAudioKey() != null)
                    ? storageService.generatePresignedUrl(current.getFrontContent().getAudioKey(), java.time.Duration.ofHours(1)) : null;
            String upcomingFrontAudio = (!isDeleted && latest != null && latest.getFrontContent() != null && latest.getFrontContent().getAudioKey() != null)
                    ? storageService.generatePresignedUrl(latest.getFrontContent().getAudioKey(), java.time.Duration.ofHours(1)) : null;
            CardDiffResponse.FieldDiff frontAudioDiff = getFieldDiff(currentFrontAudio, upcomingFrontAudio);

            String currentBackText = (current != null && current.getBackContent() != null) ? current.getBackContent().getText() : null;
            String upcomingBackText = (!isDeleted && latest != null && latest.getBackContent() != null) ? latest.getBackContent().getText() : null;
            CardDiffResponse.FieldDiff backTextDiff = getFieldDiff(currentBackText, upcomingBackText);

            String currentBackImage = (current != null && current.getBackContent() != null && current.getBackContent().getImageKey() != null)
                    ? storageService.generatePresignedUrl(current.getBackContent().getImageKey(), java.time.Duration.ofHours(1)) : null;
            String upcomingBackImage = (!isDeleted && latest != null && latest.getBackContent() != null && latest.getBackContent().getImageKey() != null)
                    ? storageService.generatePresignedUrl(latest.getBackContent().getImageKey(), java.time.Duration.ofHours(1)) : null;
            CardDiffResponse.FieldDiff backImageDiff = getFieldDiff(currentBackImage, upcomingBackImage);

            String currentBackAudio = (current != null && current.getBackContent() != null && current.getBackContent().getAudioKey() != null)
                    ? storageService.generatePresignedUrl(current.getBackContent().getAudioKey(), java.time.Duration.ofHours(1)) : null;
            String upcomingBackAudio = (!isDeleted && latest != null && latest.getBackContent() != null && latest.getBackContent().getAudioKey() != null)
                    ? storageService.generatePresignedUrl(latest.getBackContent().getAudioKey(), java.time.Duration.ofHours(1)) : null;
            CardDiffResponse.FieldDiff backAudioDiff = getFieldDiff(currentBackAudio, upcomingBackAudio);

            return CardDiffResponse.builder()
                    .cardId(card.getId())
                    .changeType(changeType)
                    .currentVersion(currentVer)
                    .upcomingVersion(upcomingVer)
                    .type(typeDiff)
                    .frontText(frontTextDiff)
                    .frontImage(frontImageDiff)
                    .frontAudio(frontAudioDiff)
                    .backText(backTextDiff)
                    .backImage(backImageDiff)
                    .backAudio(backAudioDiff)
                    .build();
        });
    }

    private CardDiffResponse.FieldDiff getFieldDiff(String current, String upcoming) {
        if (java.util.Objects.equals(current, upcoming)) {
            return null;
        }
        return new CardDiffResponse.FieldDiff(current, upcoming);
    }

    /**
     * Sync {@link UserCardProgress}'s version to {@code latest} version
     * <ul>
     * <li>If card is {@code NEW}, create new {@link UserCardProgress}</li>
     * <li>If card is {@code DELETED}, delete {@link UserCardProgress} this
     * action cannot {@code undo}</li>
     * <li>If card is {@code UPDATED}, update card version of
     * {@link UserCardProgress}</li>
     * </ul>
     *
     * @param userId the ID of user who do this action
     * @param cardIds the ID of cards need to sync
     * @param savedDeckId the ID of the saved deck.
     */
    @org.springframework.transaction.annotation.Transactional
    public void syncCard(int userId, int savedDeckId, List<Integer> cardIds) throws AppException {
        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this saved deck");
        }

        int deckId = savedDeck.getDeck().getId();
        List<CardSyncProjection> outOfSyncCards = cardRepository.findOutOfSyncCardsByIds(userId, deckId, cardIds);

        applySync(userId, outOfSyncCards, savedDeck.getUser());
    }

    /**
     * Syncs all out-of-sync cards in a saved deck.
     *
     * @param userId the ID of the requesting user.
     * @param savedDeckId the ID of the saved deck.
     * @throws AppException if the saved deck does not exist or user doesn't own
     * it.
     */
    @org.springframework.transaction.annotation.Transactional
    public void syncAll(int userId, int savedDeckId) throws AppException {
        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this saved deck");
        }

        int deckId = savedDeck.getDeck().getId();
        Page<CardSyncProjection> outOfSyncPage = cardRepository.findOutOfSyncCards(userId, deckId, Pageable.unpaged());

        applySync(userId, outOfSyncPage.getContent(), savedDeck.getUser());
    }

    private void applySync(int userId, List<CardSyncProjection> rows, vn.edu.ptithcm.mindcard.entity.User user) {
        List<UserCardProgress> toSave = new java.util.ArrayList<>();
        List<UserCardProgress> toDelete = new java.util.ArrayList<>();

        for (CardSyncProjection row : rows) {
            Card card = row.getCard();
            UserCardProgress progress = row.getProgress();

            if (card.getIsDeleted()) {
                if (progress != null) {
                    toDelete.add(progress);
                }
            } else {
                if (progress == null) {
                    UserCardProgress newProgress = UserCardProgress.builder()
                            .id(new UserCardProgress.UserCardProgressId(userId, card.getId()))
                            .user(user)
                            .card(card)
                            .cardVersion(card.getLatestVersion())
                            .status(UserCardProgress.CardStatus.NEW)
                            .easeFactor(2.5)
                            .interval(1)
                            .repetitions(0)
                            .nextReviewDate(java.time.Instant.now())
                            .build();
                    toSave.add(newProgress);
                } else {
                    progress.setCardVersion(card.getLatestVersion());
                    toSave.add(progress);
                }
            }
        }

        if (!toDelete.isEmpty()) {
            userCardProgressRepository.deleteAll(toDelete);
        }
        if (!toSave.isEmpty()) {
            userCardProgressRepository.saveAll(toSave);
        }
    }
}
