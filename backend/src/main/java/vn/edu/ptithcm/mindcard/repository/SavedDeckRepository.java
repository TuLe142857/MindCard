package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

public interface SavedDeckRepository extends JpaRepository<SavedDeck, Integer> {

    Optional<SavedDeck> findByUserIdAndDeckId(int userId, int deckId);

    @EntityGraph(attributePaths = {"deck", "user"})
    Page<SavedDeck> findByUserId(int userId, Pageable pageable);
}
