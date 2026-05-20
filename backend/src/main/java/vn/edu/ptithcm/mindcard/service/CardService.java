package vn.edu.ptithcm.mindcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import vn.edu.ptithcm.mindcard.dto.request.card.CardCreateRequest;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

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

    private String uploadFile(MultipartFile file, String keyPrefix) throws AppException {
        if (file  == null || file.isEmpty()){
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
        } catch (IOException ioException){
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return key;
    }

    @Transactional
    public void createCards(int userId, int deckId, List<CardCreateRequest> createRequests) throws AppException {
        Deck deck = deckRepository.getReferenceById(deckId);
        User user = userRepository.getReferenceById(userId);

        if (! deck.getOwner().getId().equals(user.getId()))
            throw new AppException(ErrorCode.FORBIDDEN);

        for (CardCreateRequest request: createRequests){

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

    public void getCardsInDecks(int deckId){

    }
}
