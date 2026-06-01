package vn.edu.ptithcm.mindcard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.ptithcm.mindcard.entity.Deck;

public interface DeckRepository extends JpaRepository<Deck, Integer> {

    Optional<Deck> findByOwnerIdAndName(
            int ownerId,
            String name
    );

    @Query("""
        SELECT d FROM Deck d
        WHERE d.visibility = 'PUBLIC'
          AND d.isDeleted = false
          AND (:keyword IS NULL OR :keyword = '' 
               OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(d.topic.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Deck> searchPublicDecks(@Param("keyword") String keyword, Pageable pageable);
}
