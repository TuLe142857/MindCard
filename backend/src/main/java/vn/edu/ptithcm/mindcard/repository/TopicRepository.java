package vn.edu.ptithcm.mindcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.ptithcm.mindcard.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
}
