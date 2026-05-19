package vn.edu.ptithcm.mindcard.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.ptithcm.mindcard.dto.response.topic.TopicResponse;
import vn.edu.ptithcm.mindcard.entity.Topic;
import vn.edu.ptithcm.mindcard.repository.TopicRepository;

import java.util.List;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;


    public List<TopicResponse> getAllTopics(){
        return topicRepository.findAll()
                .stream()
                .map((topic)-> new TopicResponse(topic.getId(), topic.getName()))
                .toList();
    }
}
