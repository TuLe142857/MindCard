package vn.edu.ptithcm.mindcard.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_decks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedDeck {

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SavedDeckId{
        @Column(name = "user_id")
        private Integer userId;

        @Column(name = "deck_id")
        private Integer deckId;
    }

    @EmbeddedId
    private SavedDeckId deckId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("deckId")
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
