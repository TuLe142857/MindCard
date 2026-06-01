package vn.edu.ptithcm.mindcard.dto.request.auth;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
}
