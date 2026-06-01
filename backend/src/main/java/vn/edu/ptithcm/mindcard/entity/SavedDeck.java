package vn.edu.ptithcm.mindcard.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "saved_decks",
        uniqueConstraints = {
        @UniqueConstraint(name = "unique_saved_deck", columnNames = {"user_id", "deck_id"})
        }
)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @CreationTimestamp
    @Column(name = "saved_at")
    private Instant savedAt;
}
