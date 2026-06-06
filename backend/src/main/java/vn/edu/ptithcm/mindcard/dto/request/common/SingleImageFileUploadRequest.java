package vn.edu.ptithcm.mindcard.dto.request.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.validation.AllowedContentType;

public record SingleImageFileUploadRequest(
        @AllowedContentType(types = {"image/jpeg", "image/png"})
        @Schema(description = "Allowed content types: ['image/jpeg', 'image/png']", nullable = false)
        MultipartFile file
)
{ }
