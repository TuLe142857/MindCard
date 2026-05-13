package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;

@Entity
@Table(name = "decks")
@Data
@Builder
public class Deck {
    public enum DeckVisibility{
        PUBLIC,
        PRIVATE,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private DeckVisibility visibility;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
