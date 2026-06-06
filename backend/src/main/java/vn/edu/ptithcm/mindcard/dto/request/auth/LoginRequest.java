package vn.edu.ptithcm.mindcard.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "Identity is required")
        @Schema(description = "Email or username for login credentials")
        String identity,

        @NotBlank(message = "Password is required")
        @Schema(description = "Raw password", format = "password")
        String password
){ }
