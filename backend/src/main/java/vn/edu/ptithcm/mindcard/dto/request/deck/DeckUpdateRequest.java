package vn.edu.ptithcm.mindcard.dto.request.deck;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Any null or empty field will be ignored")
public record DeckUpdateRequest (

        String name,
        String description,
        Integer topicId
){
    /**
     * Check if any field of this dto is not null
     * @return {@code true} if any field of this dto is not {@code null} else {@code false}
     */
    public boolean hasUpdateField(){
        return name != null || description != null || topicId != null;
    }
}
