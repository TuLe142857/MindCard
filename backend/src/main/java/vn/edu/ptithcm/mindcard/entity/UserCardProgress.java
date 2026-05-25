package vn.edu.ptithcm.mindcard.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCardProgress {
    public enum CardStatus{
        NEW,
        LEARNING,
        REVIEW
    }

    @Embeddable
    @Data
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
    @Column(name = "ease_factor", columnDefinition = "double precision default 2.5", nullable = false)
    private Double easeFactor = 2.5;

    @Builder.Default
    @Column(name = "interval", columnDefinition = "integer default 1", nullable = false)
    private Integer interval = 1;

    @Builder.Default
    @Column(name = "repetitions", columnDefinition = "integer default 0", nullable = false)
    private Integer repetitions = 0;

    @Builder.Default
    @Column(name = "next_review_date", columnDefinition = "timestamp default now()", nullable = false)
    private Instant nextReviewDate = Instant.now();

}
