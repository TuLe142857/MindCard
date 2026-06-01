package vn.edu.ptithcm.mindcard.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JWTProperties(
    String secretKey,
    long accessTokenExpirationSecond,
    long refreshTokenExpirationSecond,
    String accessTokenCookieName,
    String refreshTokenCookieName
) { }
