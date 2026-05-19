package vn.edu.ptithcm.mindcard.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.ptithcm.mindcard.dto.request.deck.DeckCreateRequest;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSummaryResponse;
import vn.edu.ptithcm.mindcard.entity.Deck;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.DeckRepository;
import vn.edu.ptithcm.mindcard.repository.TopicRepository;
import vn.edu.ptithcm.mindcard.repository.UserRepository;

import java.util.Objects;

@Service
public class DeckService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private TopicRepository topicRepository;


    @Transactional
    public void createDeck(int userId, DeckCreateRequest request){
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
                .name(deck.getName())
                .owner(deck.getOwner().getUsername())
                .description(deck.getDescription())
                .topic(deck.getTopic().getName())
                .totalCard(deck.getCards().size())
                .build();

    }

    @Transactional
    public void updateDeck(){

    }

    

}
