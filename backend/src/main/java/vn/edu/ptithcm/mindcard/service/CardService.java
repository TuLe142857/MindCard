package vn.edu.ptithcm.mindcard.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardVersionRepository cardVersionRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a paginated list of cards for a specific deck.
     * <ul>
     * <li>If deck is {@code PRIVATE} only the {@code owner} of the deck is
     * allowed to list its cards.</li>
     * <li>If deck is {@code PUBLIC} anyone can list its cards</li>
     * </ul>
     *
     * @param userId the ID of the user requesting the list.
     * @param deckId the ID of the deck.
     * @param pageable pagination and sorting information.
     * @return a page of cards mapped to {@link CardResponse} DTOs.
     * @throws AppException with the following {@link ErrorCode}s:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the deck is not found.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - if the user is not allowed to access
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
     * Upload file and return keys
     *
     * @param file -
     * @param keyPrefix -
     * @return -
     * @throws AppException -
     */
    private String uploadFile(MultipartFile file, String keyPrefix) throws AppException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String key = keyPrefix + UUID.randomUUID().toString();
        try {
            storageService.uploadFile(
                    key,
                    new BufferedInputStream(file.getInputStream()),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException ioException) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return key;
    }

    @Transactional
    public void createCards(int userId, int deckId, List<CardCreateRequest> createRequests) throws AppException {
        Deck deck = deckRepository.getReferenceById(deckId);
        User user = userRepository.getReferenceById(userId);

        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        for (CardCreateRequest request : createRequests) {

            String frontImageKey = uploadFile(request.frontImage(), "img_");
            String frontAudioKey = uploadFile(request.frontAudio(), "audio_");
            String backImageKey = uploadFile(request.backImage(), "img_");
            String backAudioKey = uploadFile(request.backAudio(), "audio_");

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
     *
     * @param userId the ID of the user requesting the modification.
     * @param cardId the ID of the card to retrieve.
     * @return the requested {@link Card}.
     * @throws AppException if any validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the card is not found in the database.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the deck containing the card.</li>
     * </ul>
     */
    private Card getCardForUpdate(int userId, int cardId) throws AppException{
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!card.getDeck().getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You are not owner of this card");
        }
        return card;
    }

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

    private void saveNewVersion(Card card, CardVersion newVersion) {
        newVersion = cardVersionRepository.save(newVersion);
        card.setLatestVersion(newVersion);
        cardRepository.save(card);
    }

    @Transactional
    public void update(int userId, int cardId, CardUpdateRequest updateRequest) {
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
     * @throws AppException if any validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - Bubbled up from
     * {@link #getCardForUpdate(int, int)} if the card is not found.</li>
     * <li>{@link ErrorCode#FORBIDDEN} - Bubbled up from
     * {@link #getCardForUpdate(int, int)} if the card's deck does not belong to
     * the user.</li>
     * </ul>
     * @see #getCardForUpdate(int, int)
     */
    @Transactional
    public void deleteCard(int userId, int cardId) {
        Card card = getCardForUpdate(userId, cardId);
        card.setIsDeleted(true);
        cardRepository.save(card);
    }

    @Transactional
    public void updateFrontImage(int userId, int cardId, MultipartFile file) {
        Card card = getCardForUpdate(userId, cardId);
        String key = uploadFile(file, "img_");
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getFrontContent().setImageKey(key);
        saveNewVersion(card, newVersion);
    }

    @Transactional
    public void updateFrontAudio(int userId, int cardId, MultipartFile file) {
        Card card = getCardForUpdate(userId, cardId);
        String key = uploadFile(file, "audio_");
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getFrontContent().setAudioKey(key);
        saveNewVersion(card, newVersion);
    }

    @Transactional
    public void updateBackImage(int userId, int cardId, MultipartFile file) {
        Card card = getCardForUpdate(userId, cardId);
        String key = uploadFile(file, "img_");
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getBackContent().setImageKey(key);
        saveNewVersion(card, newVersion);
    }

    @Transactional
    public void updateBackAudio(int userId, int cardId, MultipartFile file) {
        Card card = getCardForUpdate(userId, cardId);
        String key = uploadFile(file, "audio_");
        CardVersion newVersion = createNewVersionFromLatest(card);
        newVersion.getBackContent().setAudioKey(key);
        saveNewVersion(card, newVersion);
    }
}
