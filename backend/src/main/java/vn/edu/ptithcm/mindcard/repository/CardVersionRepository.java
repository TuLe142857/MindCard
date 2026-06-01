package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptithcm.mindcard.entity.CardVersion;

public interface CardVersionRepository extends JpaRepository<CardVersion, Integer> {
}
