package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;

public interface SavedDeckRepository extends JpaRepository<SavedDeck, SavedDeck.SavedDeckId> {
}
