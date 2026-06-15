package vn.edu.ptithcm.mindcard.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "user_card_progress")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardProgress {
    public enum CardStatus{
        NEW,
        LEARNING,
        REVIEW
    }

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @EqualsAndHashCode
    public static class UserCardProgressId implements Serializable {
        @Column(name = "user_id")
        private Integer userId;

        @Column(name = "card_id")
        private Integer cardId;
    }

    @EmbeddedId
    private UserCardProgressId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "card_version_id")
    private CardVersion cardVersion;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.NEW;

    @Builder.Default
    @ColumnDefault("2.5")
    @Column(name = "ease_factor", nullable = false)
    private Double easeFactor = 2.5;

    @Builder.Default
    @ColumnDefault("1")
    @Column(name = "interval", nullable = false)
    private Integer interval = 1;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "repetitions", nullable = false)
    private Integer repetitions = 0;

    @Builder.Default
    @ColumnDefault("now()")
    @Column(name = "next_review_date", nullable = false)
    private Instant nextReviewDate = Instant.now();

}
