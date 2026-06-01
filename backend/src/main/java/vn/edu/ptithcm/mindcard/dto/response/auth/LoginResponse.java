package vn.edu.ptithcm.mindcard.dto.response.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
