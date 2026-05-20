package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.ptithcm.mindcard.entity.CardVersion;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;

import java.util.List;
import java.util.Optional;

public interface SavedDeckRepository extends JpaRepository<SavedDeck, Integer> {

    Optional<SavedDeck> findByUserIdAndDeckId(int userId, int deckId);
}
