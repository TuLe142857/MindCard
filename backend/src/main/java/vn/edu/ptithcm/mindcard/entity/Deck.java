package vn.edu.ptithcm.mindcard.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "decks", uniqueConstraints = {
    @UniqueConstraint(
            name = "unique_user_deck_name",
            columnNames = {"owner_id", "name"}
    )
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    public enum DeckVisibility {
        PUBLIC,
        PRIVATE,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeckVisibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "saved_count", nullable = false)
    private int savedCount = 0;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "avg_rating", nullable = false)
    private Double avgRating = 0D;

    @Builder.Default
    @ColumnDefault("false")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "deck", fetch = FetchType.LAZY)
    private List<Card> cards;

    @OneToMany(mappedBy = "deck", fetch = FetchType.LAZY)
    private List<SavedDeck> savedDecks;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

}
