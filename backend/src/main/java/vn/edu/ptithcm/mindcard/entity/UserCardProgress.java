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
    @Column(name = "ease_factor", nullable = false)
    private Float easeFactor = 2.5F;

    @Column(name = "next_review_date")
    private Instant nextReviewDate = Instant.now();

}
