package vn.edu.ptithcm.mindcard.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.ptithcm.mindcard.dto.request.card.CardCreateRequest;
import vn.edu.ptithcm.mindcard.dto.request.card.CardUpdateRequest;
import vn.edu.ptithcm.mindcard.dto.response.card.CardContentResponse;
import vn.edu.ptithcm.mindcard.dto.response.card.CardResponse;
import vn.edu.ptithcm.mindcard.entity.Card;
import vn.edu.ptithcm.mindcard.entity.CardVersion;
import vn.edu.ptithcm.mindcard.entity.Deck;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.entity.embeded.CardContent;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.CardRepository;
import vn.edu.ptithcm.mindcard.repository.CardVersionRepository;
import vn.edu.ptithcm.mindcard.repository.DeckRepository;
import vn.edu.ptithcm.mindcard.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final CardVersionRepository cardVersionRepository;

    private final DeckRepository deckRepository;

    private final StorageService storageService;

    private final UserRepository userRepository;

    /**
     * Retrieves a paginated list of cards for a specific deck.
     * <ul>
     *     <li>If deck is {@code PRIVATE} only the {@code owner} of the deck is
     * allowed to list its cards.</li>
     *     <li>If deck is {@code PUBLIC} anyone can list its cards</li>
     * </ul>
     *
     * @param userId the ID of the user requesting the list.
     * @param deckId the ID of the deck.
     * @param pageable pagination and sorting information.
     *
     * @return a page of cards mapped to {@link CardResponse} DTOs.
     *
     * @throws AppException with the following {@link ErrorCode}s:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the deck is not found.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not allowed to access
     * this deck and its cards.</li>
     * </ul>
     */
    public Page<CardResponse> getCardList(int userId, int deckId, Pageable pageable) throws AppException {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));

        if ((deck.getVisibility() != Deck.DeckVisibility.PUBLIC)
                && (!deck.getOwner().getId().equals(userId))) {
            throw new AppException(ErrorCode.FORBIDDEN, "You are not allowed to access this deck");
        }

        Page<Card> cards = cardRepository.findByDeckId(deckId, pageable);
        return cards.map(card -> {
            CardVersion latest = card.getLatestVersion();
            CardContent front = latest.getFrontContent();
            CardContent back = latest.getBackContent();

            return CardResponse.builder()
                    .id(card.getId())
                    .type(latest.getType())
                    .front(CardContentResponse.builder()
                            .text(front != null ? front.getText() : null)
                            .imageUrl(front != null && front.getImageKey() != null ? storageService.generatePresignedUrl(front.getImageKey(), java.time.Duration.ofHours(1)) : null)
                            .audioUrl(front != null && front.getAudioKey() != null ? storageService.generatePresignedUrl(front.getAudioKey(), java.time.Duration.ofHours(1)) : null)
                            .build())
                    .back(CardContentResponse.builder()
                            .text(back != null ? back.getText() : null)
                            .imageUrl(back != null && back.getImageKey() != null ? storageService.generatePresignedUrl(back.getImageKey(), java.time.Duration.ofHours(1)) : null)
                            .audioUrl(back != null && back.getAudioKey() != null ? storageService.generatePresignedUrl(back.getAudioKey(), java.time.Duration.ofHours(1)) : null)
                            .build())
                    .build();
        });
    }


    /**
     * Creates new cards in the specified deck.
     *
     * @param userId the ID of the requesting user.
     * @param deckId the ID of the deck to add cards to.
     * @param createRequests the list of card creation requests.
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - deck not found</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck.</li>
     * </ul>
     */
    @Transactional
    public void createCards(int userId, int deckId, List<CardCreateRequest> createRequests) throws AppException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));


        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        for (CardCreateRequest request : createRequests) {

            String frontImageKey = storageService.uploadMultipartFile(
                    "img_" + UUID.randomUUID(),
                    request.frontImage()
            );

            String frontAudioKey = storageService.uploadMultipartFile(
                    "audio_" + UUID.randomUUID(),
                    request.frontAudio()
            );

            String backImageKey = storageService.uploadMultipartFile(
                    "img_" + UUID.randomUUID(),
                    request.backImage()
            );

            String backAudioKey = storageService.uploadMultipartFile(
                    "audio_" + UUID.randomUUID(),
                    request.backAudio()
            );

            CardContent frontContent = CardContent.builder()
                    .text(request.frontText())
                    .imageKey(frontImageKey)
                    .audioKey(frontAudioKey)
                    .build();

            CardContent backContent = CardContent.builder()
                    .text(request.backText())
                    .imageKey(backImageKey)
                    .audioKey(backAudioKey)
                    .build();

            Card newCard = cardRepository.save(
                    Card.builder()
                            .deck(deck)
                            .build()
            );

            CardVersion cardVersion = cardVersionRepository.save(
                    CardVersion.builder()
                            .card(newCard)
                            .version(1)
                            .type(request.type())
                            .frontContent(frontContent)
                            .backContent(backContent)
                            .build()
            );

            newCard.setLatestVersion(cardVersion);
            cardRepository.save(newCard);
        }
    }

    /**
     * Retrieves a {@link Card} for modification (e.g., {@code UPDATE}, {@code DELETE})
     * and verifies that the requesting user is the owner of the card's deck.
     * <br/>
     * This method do not check whether {@code userId} exist or not.
     *
     * @param userId the ID of the user requesting the modification.
     * @param cardId the ID of the card to retrieve.
     *
     * @return the requested {@link Card}.
     *
     * @throws AppException if any validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    private Card getCardForUpdate(int userId, int cardId) throws AppException {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!card.getDeck().getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You are not owner of this card");
        }
        return card;
    }

    /**
     * Create new card version by coppy from lastest version and increase version number.
     *
     * @param card card
     *
     * @return new card version
     */
    private CardVersion createNewVersionFromLatest(Card card) {
        CardVersion latestVersion = card.getLatestVersion();
        CardContent oldFront = latestVersion.getFrontContent();
        CardContent newFront = CardContent.builder()
                .text(oldFront != null ? oldFront.getText() : null)
                .imageKey(oldFront != null ? oldFront.getImageKey() : null)
                .audioKey(oldFront != null ? oldFront.getAudioKey() : null)
                .build();

        CardContent oldBack = latestVersion.getBackContent();
        CardContent newBack = CardContent.builder()
                .text(oldBack != null ? oldBack.getText() : null)
                .imageKey(oldBack != null ? oldBack.getImageKey() : null)
                .audioKey(oldBack != null ? oldBack.getAudioKey() : null)
                .build();

        return CardVersion.builder()
                .card(card)
                .version(latestVersion.getVersion() + 1)
                .type(latestVersion.getType())
                .frontContent(newFront)
                .backContent(newBack)
                .build();
    }

    /**
     * Save new cardversion and pin it to be the latest version.
     *
     * @param card card
     * @param newVersion new card version
     */
    private void saveNewVersion(Card card, CardVersion newVersion) {
        newVersion = cardVersionRepository.save(newVersion);
        card.setLatestVersion(newVersion);
        cardRepository.save(card);
    }

    /**
     * Update card
     *
     * @param userId userId
     * @param cardId cardId
     * @param updateRequest updateRequest
     *
     * @throws AppException with:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    @Transactional
    public void update(int userId, int cardId, CardUpdateRequest updateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Card card = getCardForUpdate(userId, cardId);
        CardVersion newVersion = createNewVersionFromLatest(card);

        if (updateRequest.type() != null) {
            newVersion.setType(updateRequest.type());
        }
        if (updateRequest.frontText() != null) {
            newVersion.getFrontContent().setText(updateRequest.frontText());
        }
        if (updateRequest.backText() != null) {
            newVersion.getBackContent().setText(updateRequest.backText());
        }

        saveNewVersion(card, newVersion);
    }

    /**
     * Soft deletes a card by setting isDeleted to true.
     *
     * @param userId the ID of the requesting user.
     * @param cardId the ID of the card to delete.
     *
     * @throws AppException if any validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     * @see #getCardForUpdate(int, int)
     */
    @Transactional
    public void deleteCard(int userId, int cardId) {
        Card card = getCardForUpdate(userId, cardId);
        card.setIsDeleted(true);
        cardRepository.save(card);
    }

    /**
     * Update front image of card. Create new card version
     *
     * @param userId userId
     * @param cardId cardId
     * @param file image file
     *
     * @throws AppException with:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    @Transactional
    public void updateFrontImage(int userId, int cardId, MultipartFile file) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Card card = getCardForUpdate(userId, cardId);
        String key = storageService.uploadMultipartFile("img_" + UUID.randomUUID(), file);
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getFrontContent().setImageKey(key);
        saveNewVersion(card, newVersion);
    }

    /**
     * Update front audio of card. Create new card version
     *
     * @param userId userId
     * @param cardId cardId
     * @param file audio file
     *
     * @throws AppException with:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    @Transactional
    public void updateFrontAudio(int userId, int cardId, MultipartFile file) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Card card = getCardForUpdate(userId, cardId);
        String key = storageService.uploadMultipartFile("audio_" + UUID.randomUUID(), file);
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getFrontContent().setAudioKey(key);
        saveNewVersion(card, newVersion);
    }

    /**
     * Update back image of card. Create new card version
     *
     * @param userId userId
     * @param cardId cardId
     * @param file image file
     *
     * @throws AppException with:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    @Transactional
    public void updateBackImage(int userId, int cardId, MultipartFile file) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Card card = getCardForUpdate(userId, cardId);
        String key = storageService.uploadMultipartFile("img_" + UUID.randomUUID(), file);
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getBackContent().setImageKey(key);
        saveNewVersion(card, newVersion);
    }

    /**
     * Update back audio of card. Create new card version
     *
     * @param userId userId
     * @param cardId cardId
     * @param file audio file
     *
     * @throws AppException with:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    @Transactional
    public void updateBackAudio(int userId, int cardId, MultipartFile file) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Card card = getCardForUpdate(userId, cardId);
        String key = storageService.uploadMultipartFile("audio_" + UUID.randomUUID(), file);
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getBackContent().setAudioKey(key);
        saveNewVersion(card, newVersion);
    }
}
