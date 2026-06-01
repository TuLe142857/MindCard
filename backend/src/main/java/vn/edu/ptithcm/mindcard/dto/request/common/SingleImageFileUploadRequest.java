package vn.edu.ptithcm.mindcard.dto.request.common;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptithcm.mindcard.validation.AllowedContentType;

public record SingleImageFileUploadRequest(
        @AllowedContentType(types = {"image/jpeg", "image/png"})
        MultipartFile file
)
{ }
