package vn.edu.ptithcm.mindcard.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.ptithcm.mindcard.config.properties.JWTProperties;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtService {
    @Getter
    public enum TokenType{
        ACCESS_TOKEN("access"),
        REFRESH_TOKEN("refresh");

        private final String type;
        private TokenType(String type){
            this.type = type;
        }
    }

    @Autowired
    JWTProperties jwtProperties;

    @Autowired
    JwtBlacklistService blacklistService;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
    }

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

    public String generateJwtToken(String subject, Map<String, Object> additionalClaims, TokenType type){
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

    public Claims validateJwtToken(String authToken, TokenType expectedType) throws AppException {
        try {
            Claims claims =  Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!type.equals(expectedType.getType())){
                throw new AppException(ErrorCode.INVALID_JWT_TOKEN, String.format("Expected type: '%s' got '%s'", expectedType.type, type));
            }

            String jti = claims.getId();
            if (jti == null){
                throw new AppException(ErrorCode.INVALID_JWT_TOKEN);
            }else if(blacklistService.isBlacklisted(jti)){
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

    public String extractAccessTokenFromRequest(HttpServletRequest request){
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer ")){
            return headerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }

        for (Cookie cookie : cookies){
            if (jwtProperties.accessTokenCookieName().equals(cookie.getName())){
                return cookie.getValue();
            }
        }

        return null;
    }

    public String extractRefreshTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }
        for (Cookie cookie : cookies){
            if (jwtProperties.refreshTokenCookieName().equals(cookie.getName())){
                return cookie.getValue();
            }
        }

        return null;
    }
};