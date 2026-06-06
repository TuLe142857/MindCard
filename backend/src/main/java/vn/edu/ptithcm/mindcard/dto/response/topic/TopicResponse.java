package vn.edu.ptithcm.mindcard.dto.response.topic;

import io.swagger.v3.oas.annotations.media.Schema;

public record TopicResponse(
        @Schema(description = "Unique ID of the topic")
        Integer id,
        @Schema(description = "Name of the topic")
        String name
) {}
