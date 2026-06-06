package vn.edu.ptithcm.mindcard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;
import vn.edu.ptithcm.mindcard.dto.response.topic.TopicResponse;
import vn.edu.ptithcm.mindcard.service.TopicService;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping("")
    @Operation(summary = "Get all topics")
    public ResponseEntity<APIResponse.Success<List<TopicResponse>>> getTopics() {
        List<TopicResponse> topics = topicService.getAllTopics();
        return ResponseEntity.ok(APIResponse.success(topics));
    }
}
