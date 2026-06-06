package vn.edu.ptithcm.mindcard.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.stereotype.Component;
import vn.edu.ptithcm.mindcard.config.properties.JWTProperties;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {
    @Getter
    public enum TokenType {
        ACCESS_TOKEN("access"),
        REFRESH_TOKEN("refresh");

        private final String type;

        TokenType(String type) {
            this.type = type;
        }
    }

    private final JWTProperties jwtProperties;

    private final JwtBlacklistService blacklistService;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
    }

    /**
     * Generates a JWT token.
     *
     * @param subject the subject (sub) of the JWT
     * @param type the type of the token
     *
     * @return the generated JWT token as a string
     */
    public String generateJwtToken(String subject, TokenType type) {
        long expired_seconds = type.equals(TokenType.ACCESS_TOKEN)
                ? jwtProperties.accessTokenExpirationSecond()
                : jwtProperties.refreshTokenExpirationSecond();

        Date expiration = new Date((new Date()).getTime() + expired_seconds * 1000L);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuedAt(new Date())
                .claim("type", type.getType())
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    /**
     * Generates a JWT token with additional claims.
     *
     * @param subject the subject (sub) of the JWT
     * @param additionalClaims map of additional claims to include
     * @param type the type of the token
     *
     * @return the generated JWT token as a string
     */
    public String generateJwtToken(String subject, Map<String, Object> additionalClaims, TokenType type) {
        long expired_seconds = type.equals(TokenType.ACCESS_TOKEN)
                ? jwtProperties.accessTokenExpirationSecond()
                : jwtProperties.refreshTokenExpirationSecond();

        Date expiration = new Date((new Date()).getTime() + expired_seconds * 1000L);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuedAt(new Date())
                .claim("type", type.getType())
                .claims(additionalClaims)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }

    /**
     * Validates a JWT token by ensuring:
     * <ul>
     *     <li>The token is structurally valid and signed correctly</li>
     *     <li>The token is not expired</li>
     *     <li>The token is not blacklisted</li>
     * </ul>
     *
     * @param authToken the JWT token string
     * @param expectedType the expected type of the token
     *
     * @return the parsed claims if validation succeeds
     *
     * @throws AppException if validation fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#INVALID_JWT_TOKEN} - if the token is malformed, unsupported, or has the wrong type.</li>
     *     <li>{@link ErrorCode#JWT_TOKEN_REVOKED} - if the token is found in the blacklist.</li>
     *     <li>{@link ErrorCode#JWT_TOKEN_EXPIRED} - if the token has expired.</li>
     * </ul>
     */
    public Claims validateJwtToken(String authToken, TokenType expectedType) throws AppException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!type.equals(expectedType.getType())) {
                throw new AppException(ErrorCode.INVALID_JWT_TOKEN, String.format("Expected type: '%s' got '%s'", expectedType.type, type));
            }

            String jti = claims.getId();
            if (jti == null) {
                throw new AppException(ErrorCode.INVALID_JWT_TOKEN);
            } else if (blacklistService.isBlacklisted(jti)) {
                throw new AppException(ErrorCode.JWT_TOKEN_REVOKED);
            }

            return claims;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
            throw new AppException(ErrorCode.JWT_TOKEN_EXPIRED);
        }
    }

    /**
     * Extracts the {@code access} token from the client request. The token will be extracted from:
     * <ul>
     *     <li>The Authorization header</li>
     *     <li>The request cookies</li>
     * </ul>
     *
     * @param request the HTTP servlet request
     *
     * @return the extracted token, or {@code null} if not provided
     */
    public String extractAccessTokenFromRequest(HttpServletRequest request) {
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            return headerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (jwtProperties.accessTokenCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * Extracts the {@code refresh} token from the client request. The token will be extracted from the request cookies.
     *
     * @param request the HTTP servlet request
     *
     * @return the extracted token, or {@code null} if not provided
     */
    public String extractRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (jwtProperties.refreshTokenCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}