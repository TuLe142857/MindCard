package vn.edu.ptithcm.mindcard.dto.request.common;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.validation.AllowedContentType;

public record SingleAudioFileUploadRequest(
        @AllowedContentType(types = {"audio/wav", "audio/mpeg"})
        MultipartFile file
) {
}
