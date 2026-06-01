package vn.edu.ptithcm.mindcard.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.ptithcm.mindcard.dto.response.card.CardDiffResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.DeckSynSummaryResponse;
import vn.edu.ptithcm.mindcard.dto.response.deck.SavedDeckResponse;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.CardRepository;
import vn.edu.ptithcm.mindcard.repository.SavedDeckRepository;
import vn.edu.ptithcm.mindcard.repository.UserCardProgressRepository;

import java.util.List;

@Service
public class SavedDeckService {

    @Autowired
    private SavedDeckRepository savedDeckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserCardProgressRepository userCardProgressRepository;

    public Page<SavedDeckResponse> listSavedDecks(int userId, Pageable pageable) {
        return savedDeckRepository.findByUserId(userId, pageable).map(
                savedDeck -> SavedDeckResponse.builder()
                        .id(savedDeck.getId())
                        .saveFrom(savedDeck.getDeck().getId())
                        .name(savedDeck.getDeck().getName())
                        .creator(savedDeck.getDeck().getOwner().getUsername())
                        .topic(savedDeck.getDeck().getTopic().getName())
                        .description(savedDeck.getDeck().getDescription())
                        .build()
        );
    }

    public DeckSynSummaryResponse checkSyncStatus(int userId, int savedDeckId) throws AppException {
        SavedDeck savedDeck;
        try {
            savedDeck = savedDeckRepository.getReferenceById(savedDeckId);
        } catch (EntityNotFoundException e) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return DeckSynSummaryResponse.builder().build();
    }

    public List<CardDiffResponse> cardsDiff(int userId, int savedDeckId) {
        return List.of();
    }

    public void syncAll(int userId, int savedDeckId) {

    }
}
