package vn.edu.ptithcm.mindcard.dto.request.auth;

import jakarta.validation.constraints.*;

public record RegisterOtpRequest(
        @NotNull
        @NotBlank
        @Email
        String email
) {
}
