package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "decks", uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_user_deck_name",
                columnNames = {"owner_id", "name"}
        )
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deck {
    public enum DeckVisibility{
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

    @OneToMany(mappedBy = "deck", fetch = FetchType.LAZY)
    private List<Card> cards;

}
