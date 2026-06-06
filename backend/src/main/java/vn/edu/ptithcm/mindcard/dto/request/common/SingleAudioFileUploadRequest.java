package vn.edu.ptithcm.mindcard.dto.request.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.validation.AllowedContentType;

public record SingleAudioFileUploadRequest(
        @AllowedContentType(types = {"audio/wav", "audio/mpeg"})
        @Schema(description = "Allowed content types: ['audio/wav', 'audio/mpeg]", nullable = false)
        MultipartFile file
) {
}
