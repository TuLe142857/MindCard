package vn.edu.ptithcm.mindcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.dto.request.card.CardCreateRequest;
import vn.edu.ptithcm.mindcard.dto.request.card.CardUpdateRequest;
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


    /**
     * Upload file and return keys
     * @param file -
     * @param keyPrefix -
     * @return -
     * @throws AppException -
     */
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

    private Card getCardForUpdate(int userId, int cardId) {
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

        if (updateRequest.type() != null){
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
