package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.edu.ptithcm.mindcard.entity.embeded.CardContent;

import java.time.Instant;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "latest_version_id")
    private CardVersion latestVersion;

    @OneToMany(mappedBy = "card")
    List<CardVersion> versions;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
