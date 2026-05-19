package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptithcm.mindcard.entity.Deck;

import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Integer> {

    Optional<Deck> findByOwnerIdAndName(
            int ownerId,
            String name
    );
}
