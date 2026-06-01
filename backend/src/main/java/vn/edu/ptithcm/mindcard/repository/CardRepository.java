package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptithcm.mindcard.entity.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {
    Page<Card> findByDeckId(int deckId, Pageable pageable);
}
