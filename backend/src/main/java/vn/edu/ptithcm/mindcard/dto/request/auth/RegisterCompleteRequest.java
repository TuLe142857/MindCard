package vn.edu.ptithcm.mindcard.dto.request.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCompleteRequest(
        @Email
        @NotNull
        @NotBlank
        String email,

        @NotNull
        @NotBlank
        String username,

        @NotNull
        @NotBlank
        String password,

        @NotNull
        @NotBlank
        String otp
){}