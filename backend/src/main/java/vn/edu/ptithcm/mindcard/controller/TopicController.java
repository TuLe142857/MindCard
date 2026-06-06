package vn.edu.ptithcm.mindcard.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.topic.TopicResponse;
import vn.edu.ptithcm.mindcard.service.TopicService;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping("")
    public ResponseEntity<APIResponse.Success<List<TopicResponse>>> getTopics(){
        List<TopicResponse> topics = topicService.getAllTopics();
        return ResponseEntity.ok(APIResponse.success(topics));
    }
}
