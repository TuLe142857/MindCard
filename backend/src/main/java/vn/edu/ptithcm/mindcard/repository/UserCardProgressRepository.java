package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.ptithcm.mindcard.entity.UserCardProgress;

import java.util.List;

public interface UserCardProgressRepository extends JpaRepository<UserCardProgress, UserCardProgress.UserCardProgressId> {

    @Query("""
    SELECT cp FROM UserCardProgress cp
    JOIN FETCH cp.cardVersion
    WHERE cp.user.id = :userId
      AND cp.card.id IN (
          SELECT c.id
          FROM Card c
          JOIN c.deck d
          JOIN d.savedDecks sd
          WHERE sd.id = :savedDeckId
      )
      AND cp.nextReviewDate <= CURRENT_DATE
    ORDER BY cp.nextReviewDate ASC
""")
    List<UserCardProgress> findDueCardProgress(int userId, int savedDeckId, Pageable pageable);


    @Query("""
    SELECT cp FROM UserCardProgress cp
    JOIN FETCH cp.cardVersion
    WHERE cp.user.id = :userId
      AND cp.card.id IN (
          SELECT c.id
          FROM Card c
          JOIN c.deck d
          JOIN d.savedDecks sd
          WHERE sd.id = :savedDeckId
      )
      AND cp.status = 'NEW'
""")
    List<UserCardProgress> findNew(int userId, int deckId, Pageable pageable);
}
