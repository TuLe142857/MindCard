package vn.edu.ptithcm.mindcard.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptithcm.mindcard.dto.response.card.CardContentResponse;
import vn.edu.ptithcm.mindcard.dto.response.card.CardResponse;
import vn.edu.ptithcm.mindcard.entity.Card;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.entity.UserCardProgress;
import vn.edu.ptithcm.mindcard.entity.embeded.CardContent;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.CardRepository;
import vn.edu.ptithcm.mindcard.repository.SavedDeckRepository;
import vn.edu.ptithcm.mindcard.repository.UserCardProgressRepository;
import vn.edu.ptithcm.mindcard.repository.UserRepository;
import vn.edu.ptithcm.mindcard.service.algorithm.SpaceRepetitionAlgorithm;

import java.time.Duration;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final UserCardProgressRepository userCardProgressRepository;

    private final UserRepository userRepository;

    private final CardRepository cardRepository;

    private final StorageService storageService;

    private final SavedDeckRepository savedDeckRepository;

    private final SpaceRepetitionAlgorithm spaceRepetitionAlgorithm;

    /**
     * Map {@link CardContent} to {@link CardContentResponse}
     *
     * @param content entity
     *
     * @return dto
     */
    private CardContentResponse mapCardContent(CardContent content) {
        var builder = CardContentResponse.builder();
        builder.text(content.getText());
        if (content.getImageKey() != null) {
            String url = storageService.generatePresignedUrl(content.getImageKey(), Duration.ofMinutes(15));
            builder.imageUrl(url);
        }
        if (content.getAudioKey() != null) {
            String url = storageService.generatePresignedUrl(content.getAudioKey(), Duration.ofMinutes(15));
            builder.audioUrl(url);
        }

        return builder.build();
    }

    /**
     * Retrieves a batch of new cards for studying.
     *
     * @param userId the ID of the user.
     * @param savedDeckId the ID of the saved deck.
     * @param limit the maximum number of new cards to fetch.
     *
     * @return a list of {@link CardResponse} DTOs.
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - saved-deck not found</li>
     *     <li>{@link ErrorCode#FORBIDDEN} user is not owner of saved-deck</li>
     * </ul>
     */
    public List<CardResponse> getNewCardsBatch(int userId, int savedDeckId, int limit) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You are not the owner of this saved-deck");
        }

        Pageable pageable = PageRequest.of(0, limit);

        var userCardProgressList = userCardProgressRepository.findNew(userId, savedDeckId, pageable);

        return userCardProgressList.stream()
                .map(cardProgress -> {
                            var card = cardProgress.getCard();
                            var cardVersion = cardProgress.getCardVersion();
                            var frontContent = mapCardContent(cardVersion.getFrontContent());
                            var backContent = mapCardContent(cardVersion.getBackContent());

                            return CardResponse.builder()
                                    .id(card.getId())
                                    .type(cardVersion.getType())
                                    .front(frontContent)
                                    .back(backContent)
                                    .build();

                        }
                )
                .toList();

    }

    /**
     * Retrieves a batch of due cards for reviewing.
     *
     * @param userId the ID of the user.
     * @param savedDeckId the ID of the saved deck.
     * @param limit the maximum number of due cards to fetch.
     *
     * @return a list of {@link CardResponse} DTOs.
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - saved-deck not found</li>
     *     <li>{@link ErrorCode#FORBIDDEN} user is not owner of saved-deck</li>
     * </ul>
     */
    public List<CardResponse> getDueCardBatch(int userId, int savedDeckId, int limit) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SavedDeck savedDeck = savedDeckRepository.findById(savedDeckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Saved deck not found"));

        if (!savedDeck.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You are not the owner of this saved-deck");
        }

        Pageable pageable = PageRequest.of(0, limit);

        var userCardProgressList = userCardProgressRepository.findDueCardProgress(userId, savedDeckId, pageable);

        return userCardProgressList.stream()
                .map(cardProgress -> {
                            var card = cardProgress.getCard();
                            var cardVersion = cardProgress.getCardVersion();
                            var frontContent = mapCardContent(cardVersion.getFrontContent());
                            var backContent = mapCardContent(cardVersion.getBackContent());

                            return CardResponse.builder()
                                    .id(card.getId())
                                    .type(cardVersion.getType())
                                    .front(frontContent)
                                    .back(backContent)
                                    .build();

                        }
                )
                .toList();
    }

    /**
     * Updates the review quality of a card and calculates the next review schedule using the Space Repetition algorithm.
     *
     * @param userId the ID of the user.
     * @param cardId the ID of the card being reviewed.
     * @param quality the quality of the review (e.g., 0-5).
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - card or user-card-progress not found</li>
     * </ul>
     */
    @Transactional
    public void setCardReviewQuality(int userId, int cardId, int quality) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Card not found"));

        var progressId = UserCardProgress.UserCardProgressId.builder()
                .userId(userId)
                .cardId(cardId)
                .build();


        UserCardProgress progress = userCardProgressRepository.findById(progressId)
                .orElseThrow(() -> {
                    String message = "User Card Progress not found. " +
                            "Please saved deck to start study or sync your saved deck to get latest update";
                    return new AppException(ErrorCode.RESOURCE_NOT_FOUND, message);
                });

        var repetitionResult = spaceRepetitionAlgorithm.calculate(
                progress.getEaseFactor(),
                progress.getInterval(),
                progress.getRepetitions(),
                quality
        );

        progress.setEaseFactor(repetitionResult.easinessFactor());
        progress.setInterval(repetitionResult.interval());
        progress.setRepetitions(repetitionResult.repetitions());
        progress.setNextReviewDate(repetitionResult.nextReview());

        if (repetitionResult.repetitions() >= 3) {
            progress.setStatus(UserCardProgress.CardStatus.REVIEW);
        } else {
            progress.setStatus(UserCardProgress.CardStatus.LEARNING);
        }

        userCardProgressRepository.save(progress);
    }
}
