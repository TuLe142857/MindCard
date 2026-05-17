package vn.edu.ptithcm.mindcard.entity;


import jakarta.persistence.*;
import lombok.*;

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
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserCardProgressId{
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

}
