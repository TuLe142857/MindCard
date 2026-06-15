package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "deck_ratings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeckRating{

    @Embeddable
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeckRatingId{
        @Column(name = "deck_id")
        private Integer deckId;

        @Column(name = "user_id")
        private Integer userId;
    }

    @EmbeddedId
    private DeckRatingId id;

    @ManyToOne
    @MapsId("deckId")
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
