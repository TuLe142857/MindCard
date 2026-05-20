package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import vn.edu.ptithcm.mindcard.entity.embeded.CardContent;

import java.time.Instant;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Card.CardType type;

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

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}
