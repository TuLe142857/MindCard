package vn.edu.ptithcm.mindcard.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckCreateRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckQueryRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckUpdateRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.PublicDeckQueryRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.UpdateDeckVisibilityRequest;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
import vn.edu.ptithcm.mindcard.entity.Deck;
import vn.edu.ptithcm.mindcard.entity.DeckRating;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.entity.UserCardProgress;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.DeckRatingRepository;
import vn.edu.ptithcm.mindcard.repository.DeckRepository;
import vn.edu.ptithcm.mindcard.repository.SavedDeckRepository;
import vn.edu.ptithcm.mindcard.repository.TopicRepository;
import vn.edu.ptithcm.mindcard.repository.UserCardProgressRepository;
import vn.edu.ptithcm.mindcard.repository.UserRepository;
import vn.edu.ptithcm.mindcard.repository.specification.DeckSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final UserRepository userRepository;

    private final DeckRepository deckRepository;

    private final SavedDeckRepository savedDeckRepository;

    private final DeckRatingRepository deckRatingRepository;

    private final TopicRepository topicRepository;

    private final UserCardProgressRepository userCardProgressRepository;

    /**
     * Creates a new deck for the specified user.
     *
     * @param userId the ID of the user creating the deck
     * @param request the details of the deck to create
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *
     *     <li>{@link ErrorCode#RESOURCE_ALREADY_EXIST} - if a deck with the same name already exists for the user.</li>
     * </ul>
     */
    @Transactional
    public void createDeck(int userId, DeckCreateRequest request) throws AppException {
        if (deckRepository.findByOwnerIdAndName(userId, request.name()).isPresent()) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXIST, "Deck name already existed");
        }
        Deck newDeck = Deck.builder()
                .name(request.name())
                .visibility(request.visibility())
                .topic(topicRepository.getReferenceById(request.topicId()))
                .owner(userRepository.getReferenceById(userId))
                .build();
        deckRepository.save(newDeck);
    }

    /**
     * Check permission and return deck summary info
     *
     * @param userId viewer id
     * @param deckId deck id
     *
     * @return deck summary
     *
     * @throws AppException for:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - deck not found</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - Deck is private and user is not owner</li>
     * </ul>
     */
    public DeckSummaryResponse getDeckSummary(int userId, int deckId) throws AppException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));

        if (deck.getVisibility() == Deck.DeckVisibility.PRIVATE
                && !Objects.equals(deck.getOwner().getId(), user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Deck is private");
        }

        return mapToDeckSummaryResponse(deck, userId);
    }

    /**
     * Save deck and create CardProgress(with status=NEW), update
     * deck.savedCount
     *
     * @param userId user id
     * @param deckId deck id
     *
     * @throws AppException with:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - deck not found</li>
     *     <li>{@link ErrorCode#FORBIDDEN} deck is private and user is not owner</li>
     * </ul>
     */
    @Transactional
    public void saveDeck(int userId, int deckId) throws AppException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));

        // Check if deck is private and not owned by the user
        if (deck.getVisibility() == Deck.DeckVisibility.PRIVATE && !deck.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Cannot save a private deck");
        }

        // Check if user has already saved this deck
        if (savedDeckRepository.findByUserIdAndDeckId(userId, deckId).isPresent()) {
            throw new AppException(ErrorCode.ACTION_ALREADY_PERFORMED, "You have already saved this deck!");
        }

        // save deck
        savedDeckRepository.save(
                SavedDeck.builder()
                        .user(user)
                        .deck(deck)
                        .name(deck.getName())
                        .description(deck.getDescription())
                        .build()
        );

        // create card progress
        deck.getCards().forEach(card -> {
            var id = UserCardProgress.UserCardProgressId.builder()
                    .userId(user.getId())
                    .cardId(card.getId())
                    .build();
            userCardProgressRepository.save(
                    UserCardProgress.builder()
                            .id(id)
                            .user(user)
                            .card(card)
                            .status(UserCardProgress.CardStatus.NEW)
                            .cardVersion(card.getLatestVersion())
                            .build()
            );
        });

        // update save count
        deck.setSavedCount(deck.getSavedCount() + 1);
        deckRepository.save(deck);
    }

    /**
     * Add rating to deck and update deck.ratingCount, deck.avgRating
     *
     * @param userId user id
     * @param deckId deck id
     * @param rating rating in range [1, 5]
     *
     * @throws AppException with the following {@link ErrorCode}
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND}</li> - deck not found
     *     <li>{@link ErrorCode#FORBIDDEN} deck is private and user is not owner</li>
     *     <li>{@link ErrorCode#ACTION_ALREADY_PERFORMED}</li> - user has already
     * rating this deck
     * </ul>
     * @throws IllegalArgumentException when {@code rating} is not in range [1,
     * 5]
     */
    public void ratingDeck(int userId, int deckId, int rating) throws AppException, IllegalArgumentException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));

        if (deck.getVisibility() == Deck.DeckVisibility.PRIVATE && !deck.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Deck is private");
        }

        if (deckRatingRepository.findByDeckIdAndUserId(deckId, userId).isPresent()) {
            throw new AppException(ErrorCode.ACTION_ALREADY_PERFORMED, "You have already rating this deck!");
        }

        if (!(rating >= 1 && rating <= 5)) {
            throw new IllegalArgumentException("rating must in [1, 5]");
        }

        var ratingId = DeckRating.DeckRatingId.builder()
                .userId(userId)
                .deckId(deckId)
                .build();

        // set rating
        deckRatingRepository.save(
                DeckRating.builder()
                        .id(ratingId)
                        .user(user)
                        .deck(deck)
                        .rating(rating)
                        .build()
        );

        // update avg rating
        int ratingCount = deck.getRatingCount();
        double avgRating = deck.getAvgRating();
        deck.setRatingCount(ratingCount + 1);
        deck.setAvgRating((avgRating * ratingCount + rating) / (ratingCount + 1));

        deckRepository.save(deck);
    }

    /**
     * Update Deck
     *
     * @param updateRequest update request
     *
     * @throws AppException ...
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND}</li>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND}</li> - deck not found
     *     <li>{@link ErrorCode#FORBIDDEN} deck is private and user is not owner</li>
     * rating this deck
     * </ul>
     */
    @Transactional
    public void updateDeck(int userId, int deckId, DeckUpdateRequest updateRequest) throws AppException {
        userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
        Deck deck = deckRepository.findById(deckId).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        // check permission
        if (!deck.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You're not owner of this deck!");
        }

        if (updateRequest == null || !updateRequest.hasUpdateField()) {
            return;
        }

        if (updateRequest.name() != null && !updateRequest.name().equals(deck.getName())) {
            if (deckRepository.findByOwnerIdAndName(userId, updateRequest.name()).isPresent()) {
                throw new AppException(ErrorCode.RESOURCE_ALREADY_EXIST, "Deck name already exist");
            }
            deck.setName(updateRequest.name());
        }

        if (updateRequest.description() != null) {
            deck.setDescription(updateRequest.description());
        }

        if (updateRequest.topicId() != null
                && !deck.getTopic().getId().equals(updateRequest.topicId())) {
            try {
                deck.setTopic(topicRepository.getReferenceById(updateRequest.topicId()));
            } catch (EntityNotFoundException e) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Topic not found");
            }
        }

        deckRepository.save(deck);
    }

    /**
     * Updates the visibility of a deck.
     *
     * @param userId the ID of the requesting user.
     * @param deckId the ID of the deck to update.
     * @param request the request containing the new visibility.
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - deck not found</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the deck does not belong to the user.</li>
     * </ul>
     */
    @Transactional
    public void updateDeckVisibility(int userId, int deckId, UpdateDeckVisibilityRequest request) throws AppException {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));

        if (!deck.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You do not own this deck");
        }

        deck.setVisibility(request.visibility());
        deckRepository.save(deck);
    }

    /**
     * Soft deletes a deck by setting isDeleted to true.
     *
     * @param userId the ID of the requesting user.
     * @param deckId the ID of the deck to delete.
     *
     * @throws AppException if any validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND} - if the deck is not found.</li>
     *     <li>{@link ErrorCode#FORBIDDEN} - if the user is not the owner of the
     * deck.</li>
     * </ul>
     */
    @Transactional
    public void deleteDeck(int userId, int deckId) throws AppException {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Deck not found"));

        if (!deck.getOwner().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "You are not owner of this deck");
        }

        deck.setIsDeleted(true);
        deckRepository.save(deck);
    }

    /**
     * Searches for public decks using specification filters and pagination.
     *
     * @param queryRequest the query filters including keyword, topicId, and
     * sorting
     *
     * @return a page of public decks mapped to {@link DeckSummaryResponse} DTOs
     */
    public Page<DeckSummaryResponse> searchPublicDecks(int userId, PublicDeckQueryRequest queryRequest) {
        Specification<Deck> spec = Specification.where(DeckSpecification.isDeleted(false))
                .and(DeckSpecification.hasVisibility(Deck.DeckVisibility.PUBLIC))
                .and(DeckSpecification.hasKeyword(queryRequest.keyword()))
                .and(DeckSpecification.hasTopicId(queryRequest.topicId()));

        Page<Deck> decks = deckRepository.findAll(spec, queryRequest.toPageable());
        return decks.map(deck -> mapToDeckSummaryResponse(deck, userId));
    }

    /**
     * Retrieves all decks (both {@code PUBLIC} and {@code PRIVATE}) belonging
     * to a specific user.
     *
     * @param userId the ID of the deck owner
     * @param query pageable and query
     *
     * @return a page of decks mapped to {@link DeckSummaryResponse} DTOs
     *
     * @throws AppException with the following {@link ErrorCode}:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND} - if the user with specified userId
     * is not found.</li>
     * </ul>
     */
    public Page<DeckSummaryResponse> getUserDecks(int userId, DeckQueryRequest query) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Specification<Deck> spec = Specification.where(DeckSpecification.isDeleted(false))
                .and(DeckSpecification.hasOwnerId(userId))
                .and(DeckSpecification.hasKeyword(query.keyword()))
                .and(DeckSpecification.hasTopicId(query.topicId()))
                .and(DeckSpecification.hasVisibility(query.visibility()));

        Page<Deck> decks = deckRepository.findAll(spec, query.toPageable());
        return decks.map(deck -> mapToDeckSummaryResponse(deck, userId));
    }

    /**
     * Retrieves only {@code PUBLIC} decks belonging to a user identified by
     * username.
     *
     * @param username the username of the deck owner
     * @param query pagination query
     *
     * @return a page of public decks mapped to {@link DeckSummaryResponse} DTOs
     *
     * @throws AppException with the following {@link ErrorCode}:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND} - if the user with the specified
     * username is not found.</li>
     * </ul>
     */
    public Page<DeckSummaryResponse> getPublicDecksByUsername(int userId, String username, PublicDeckQueryRequest query) throws AppException {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Specification<Deck> spec = Specification.where(DeckSpecification.isDeleted(false))
                .and(DeckSpecification.hasOwnerUsername(username))
                .and(DeckSpecification.hasVisibility(Deck.DeckVisibility.PUBLIC))
                .and(DeckSpecification.hasKeyword(query.keyword()))
                .and(DeckSpecification.hasTopicId(query.topicId()));
        Page<Deck> decks = deckRepository.findAll(spec, query.toPageable());
        return decks.map(deck -> mapToDeckSummaryResponse(deck, userId));
    }

    private DeckSummaryResponse mapToDeckSummaryResponse(Deck deck, int userId) {
        boolean isSaved = savedDeckRepository.findByUserIdAndDeckId(userId, deck.getId()).isPresent();
        Integer userRating = deckRatingRepository.findByDeckIdAndUserId(deck.getId(), userId)
                .map(DeckRating::getRating)
                .orElse(null);

        return DeckSummaryResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .owner(deck.getOwner().getUsername())
                .topic(deck.getTopic().getName())
                .visibility(deck.getVisibility())
                .description(deck.getDescription())
                .totalCard(deck.getCards().size())
                .savedCount(deck.getSavedCount())
                .ratingCount(deck.getRatingCount())
                .avgRating(deck.getAvgRating())
                .createdAt(deck.getCreatedAt())
                .isSaved(isSaved)
                .userRating(userRating)
                .build();
    }

}
