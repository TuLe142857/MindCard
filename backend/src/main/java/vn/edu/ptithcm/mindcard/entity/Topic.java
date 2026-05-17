package vn.edu.ptithcm.mindcard.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;

import java.util.List;

@Entity
@Table(name = "topics")
@Data
@Builder
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 64)
    private String name;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    List<Deck> decks;
}
