package vn.edu.ptithcm.mindcard.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationContextException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckCreateRequest;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckUpdateRequest;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
import vn.edu.ptithcm.mindcard.entity.*;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.Optional;

@Service
public class DeckService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private SavedDeckRepository savedDeckRepository;

    @Autowired
    private DeckRatingRepository deckRatingRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserCardProgressRepository userCardProgressRepository;


    @Transactional
    public void createDeck(int userId, DeckCreateRequest request) throws AppException{
        if (deckRepository.findByOwnerIdAndName(userId, request.name()).isPresent())
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXIST, "Deck name already existed");
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
     * @param userId viewer id
     * @param deckId deck id
     * @return deck summary
     */
    public DeckSummaryResponse getDeckSummary(int userId, int deckId){
        User user;
        Deck deck;
        try{
            user = userRepository.getReferenceById(userId);
            deck = deckRepository.getReferenceById(deckId);
        }catch (EntityNotFoundException e){
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (deck.getVisibility() == Deck.DeckVisibility.PRIVATE
                && !Objects.equals(deck.getOwner().getId(), user.getId())
        ){
            throw new AppException(ErrorCode.FORBIDDEN, "Deck is private");
        }

        return DeckSummaryResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .owner(deck.getOwner().getUsername())
                .description(deck.getDescription())
                .topic(deck.getTopic().getName())
                .totalCard(deck.getCards().size())
                .savedCount(deck.getSavedCount())
                .ratingCount(deck.getRatingCount())
                .avgRating(deck.getAvgRating())
                .build();

    }

    /**
     * Save deck and create CardProgress(with status=NEW), update deck.savedCount
     * @param userId user id
     * @param deckId deck id
     */
    @Transactional
    public void saveDeck(int userId, int deckId) throws AppException{
        User user;
        Deck deck;
        try{
            user = userRepository.getReferenceById(userId);
            deck = deckRepository.getReferenceById(deckId);
        }catch (EntityNotFoundException e){
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage());
        }

        // Check if user has already saved this deck
        if (savedDeckRepository.findByUserIdAndDeckId(userId, deckId).isPresent()){
            throw new AppException(ErrorCode.ACTION_ALREADY_PERFORMED, "You have already saved this deck!");
        }

        // save deck
        savedDeckRepository.save(
                SavedDeck.builder()
                        .user(user)
                        .deck(deck)
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
     * @param userId user id
     * @param deckId deck id
     * @param rating rating in range [1, 5]
     * @throws AppException with the following {@link ErrorCode}
     * <ul>
     *     <li>{@link ErrorCode#RESOURCE_NOT_FOUND}</li> - user or deck not found
     *     <li>{@link ErrorCode#ACTION_ALREADY_PERFORMED}</li> - user has already rating this deck
     * </ul>
     * @throws IllegalArgumentException when {@code rating} is not in range [1, 5]
     */
    public void ratingDeck(int userId, int deckId, int rating) throws AppException, IllegalArgumentException{
        User user;
        Deck deck;
        try{
            user = userRepository.getReferenceById(userId);
            deck = deckRepository.getReferenceById(deckId);
        }catch (EntityNotFoundException e){
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage());
        }

        if (deckRatingRepository.findByDeckIdAndUserId(deckId, userId).isPresent()){
            throw new AppException(ErrorCode.ACTION_ALREADY_PERFORMED,"You have already rating this deck!");
        }

        if(!(rating >= 1 && rating <= 5)){
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
        deck.setAvgRating( (avgRating*ratingCount + rating) / (ratingCount + 1));

        deckRepository.save(deck);
    }


    /**
     * Update Deck
     * @param updateRequest update request
     * @throws AppException ...
     */
    @Transactional
    public void updateDeck(int userId, int deckId, DeckUpdateRequest updateRequest) throws AppException{
        User user;
        Deck deck;
        try{
            user = userRepository.getReferenceById(userId);
            deck = deckRepository.getReferenceById(deckId);
        }catch (EntityNotFoundException e){
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage());
        }

        // check permission
        if (! deck.getOwner().getId().equals(userId))
            throw new AppException(ErrorCode.FORBIDDEN, "You're not owner of this deck!");

        if(updateRequest == null || !updateRequest.hasUpdateField())
            return;

        if (updateRequest.name() != null && !updateRequest.name().equals(deck.getName())){
            if (deckRepository.findByOwnerIdAndName(userId, updateRequest.name()).isPresent())
                throw new AppException(ErrorCode.RESOURCE_ALREADY_EXIST, "Deck name already exist");
            deck.setName(updateRequest.name());
        }

        if (updateRequest.description() != null){
            deck.setDescription(updateRequest.description());
        }

        if (
                updateRequest.topicId() != null
                && !deck.getTopic().getId().equals(updateRequest.topicId())
        ){
            try{
                deck.setTopic(topicRepository.getReferenceById(updateRequest.topicId()));
            }catch (EntityNotFoundException e){
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Topic not found");
            }
        }

        deckRepository.save(deck);
    }

    /**
     * Searches for public decks by keyword with pagination.
     *
     * @param keyword the search keyword matching name, description, or topic name
     * @param pageable pagination and sorting information
     * @return a page of public decks mapped to {@link DeckSummaryResponse} DTOs
     */
    public Page<DeckSummaryResponse> searchPublicDecks(String keyword, Pageable pageable) {
        Page<Deck> decks = deckRepository.searchPublicDecks(keyword, pageable);
        return decks.map(deck -> DeckSummaryResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .owner(deck.getOwner().getUsername())
                .topic(deck.getTopic().getName())
                .description(deck.getDescription())
                .totalCard(deck.getCards().size())
                .savedCount(deck.getSavedCount())
                .ratingCount(deck.getRatingCount())
                .avgRating(deck.getAvgRating())
                .build());
    }

    /**
     * Retrieves all decks (both {@code PUBLIC} and {@code PRIVATE}) belonging to a specific user.
     *
     * @param userId the ID of the deck owner
     * @param pageable pagination and sorting information
     * @return a page of decks mapped to {@link DeckSummaryResponse} DTOs
     * @throws AppException with the following {@link ErrorCode}:
     * <ul>
     *     <li>{@link ErrorCode#USER_NOT_FOUND} - if the user with specified userId is not found.</li>
     * </ul>
     */
    public Page<DeckSummaryResponse> getUserDecks(int userId, Pageable pageable) throws AppException{
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Page<Deck> decks = deckRepository.findAllByOwnerId(userId, pageable);
        return decks.map(deck -> DeckSummaryResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .owner(deck.getOwner().getUsername())
                .topic(deck.getTopic().getName())
                .description(deck.getDescription())
                .totalCard(deck.getCards().size())
                .savedCount(deck.getSavedCount())
                .ratingCount(deck.getRatingCount())
                .avgRating(deck.getAvgRating())
                .build());
    }

    /**
     * Retrieves only {@code PUBLIC} decks belonging to a user identified by username.
     *
     * @param username the username of the deck owner
     * @param pageable pagination and sorting information
     * @return a page of public decks mapped to {@link DeckSummaryResponse} DTOs
     * @throws AppException with the following {@link ErrorCode}:
     * <ul>
     * <li>{@link ErrorCode#USER_NOT_FOUND} - if the user with the specified username is not found.</li>
     * </ul>
     */
    public Page<DeckSummaryResponse> getPublicDecksByUsername(String username, Pageable pageable) throws AppException {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Page<Deck> decks = deckRepository.findPublicByOwnerUsername(username, pageable);
        return decks.map(deck -> DeckSummaryResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .owner(deck.getOwner().getUsername())
                .topic(deck.getTopic().getName())
                .description(deck.getDescription())
                .totalCard(deck.getCards().size())
                .savedCount(deck.getSavedCount())
                .ratingCount(deck.getRatingCount())
                .avgRating(deck.getAvgRating())
                .build());
    }

}
