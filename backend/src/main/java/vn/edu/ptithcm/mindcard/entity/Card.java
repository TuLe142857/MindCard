package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.ptithcm.mindcard.entity.embeded.CardContent;

import java.time.Instant;

@Entity
@Table(name = "cards")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    public enum CardType{
        BASIC,
        TYPE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "front_text")),
            @AttributeOverride(name = "imageKey", column = @Column(name = "front_image_key")),
            @AttributeOverride(name = "audioKey", column = @Column(name = "front_audio_key"))
    })
    private CardContent frontContent;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "back_text")),
            @AttributeOverride(name = "imageKey", column = @Column(name = "back_image_key")),
            @AttributeOverride(name = "audioKey", column = @Column(name = "back_audio_key"))
    })
    private CardContent backContent;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @Column(name = "avg_rating", nullable = false)
    private Double AvgRating = 0D;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
