package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptithcm.mindcard.entity.DeckRating;

public interface DeckRatingRepository extends JpaRepository<DeckRating, DeckRating.DeckRatingId > {
}
