package vn.edu.ptithcm.mindcard.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.ptithcm.mindcard.entity.Card;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.entity.UserCardProgress;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.CardRepository;
import vn.edu.ptithcm.mindcard.repository.UserCardProgressRepository;
import vn.edu.ptithcm.mindcard.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class StudyService {
    @Autowired
    private UserCardProgressRepository userCardProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    public void getStudyCardsBatch(int userId, int deckId, int limit){

    }

    public void setCardReviewQuality(int userId, int cardId, int quality){
        User user;
        Card card;
        UserCardProgress progress;
        try{
            user = userRepository.getReferenceById(userId);
            card = cardRepository.getReferenceById(cardId);
            var progressId = UserCardProgress.UserCardProgressId.builder()
                    .userId(userId)
                    .cardId(cardId)
                    .build();
            progress = userCardProgressRepository.getReferenceById(progressId);
        }catch (EntityNotFoundException e){
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // implement SM-2
        // coming soon...

        progress.setNextReviewDate(
                Instant.now().plus(3, ChronoUnit.DAYS)
        );
    }
}
