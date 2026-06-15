package vn.edu.ptithcm.mindcard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.ptithcm.mindcard.entity.Card;
import vn.edu.ptithcm.mindcard.repository.projection.CardSyncProjection;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Page<Card> findByDeckId(int deckId, Pageable pageable);

    @Query(value = """
    SELECT c AS card, cp AS progress FROM Card c
    LEFT JOIN c.latestVersion clv
    LEFT JOIN UserCardProgress cp ON cp.card = c AND cp.user.id = :userId
    LEFT JOIN cp.cardVersion cpv
    WHERE c.deck.id = :deckId
      AND (
          (c.isDeleted = false AND cp IS NULL)
          OR (c.isDeleted = false AND cpv.version < clv.version)
          OR (c.isDeleted = true AND cp IS NOT NULL)
      )
    """,
            countQuery = """
    SELECT COUNT(c) FROM Card c
    LEFT JOIN c.latestVersion clv
    LEFT JOIN UserCardProgress cp ON cp.card = c AND cp.user.id = :userId
    LEFT JOIN cp.cardVersion cpv
    WHERE c.deck.id = :deckId
      AND (
          (c.isDeleted = false AND cp IS NULL)
          OR (c.isDeleted = false AND cpv.version < clv.version)
          OR (c.isDeleted = true AND cp IS NOT NULL)
      )
    """)
    Page<CardSyncProjection> findOutOfSyncCards(int userId, int deckId, Pageable pageable);

    @Query(value = """
    SELECT c AS card, cp AS progress FROM Card c
    LEFT JOIN c.latestVersion clv
    LEFT JOIN UserCardProgress cp ON cp.card = c AND cp.user.id = :userId
    LEFT JOIN cp.cardVersion cpv
    WHERE c.deck.id = :deckId AND c.id IN :cardIds
      AND (
          (c.isDeleted = false AND cp IS NULL)
          OR (c.isDeleted = false AND cpv.version < clv.version)
          OR (c.isDeleted = true AND cp IS NOT NULL)
      )
    """)
    List<CardSyncProjection> findOutOfSyncCardsByIds(int userId, int deckId, java.util.List<Integer> cardIds);
}
