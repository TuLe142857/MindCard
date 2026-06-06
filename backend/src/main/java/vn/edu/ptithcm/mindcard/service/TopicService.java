package vn.edu.ptithcm.mindcard.service;


import org.springframework.stereotype.Service;
import vn.edu.ptithcm.mindcard.dto.response.topic.TopicResponse;
import vn.edu.ptithcm.mindcard.entity.Topic;
import vn.edu.ptithcm.mindcard.repository.TopicRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;


    public List<TopicResponse> getAllTopics(){
        return topicRepository.findAll()
                .stream()
                .map((topic)-> new TopicResponse(topic.getId(), topic.getName()))
                .toList();
    }
}
