package vn.edu.ptithcm.mindcard.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Adds a JWT token to the blacklist.
     * If {@code jti} or {@code expiration} is null, this function does nothing.
     * If the token is already expired, this function does nothing.
     *
     * @param jti UUID of the JWT token
     * @param expiration expiration date
     */
    public void addToBlackList(String jti, Date expiration) {
        if (jti == null || expiration == null) {
            return;
        }

        Date now = new Date();
        if (now.after(expiration)) {
            return;
        }
        long ttlMillis = expiration.getTime() - now.getTime();
        redisTemplate.opsForValue().set("jwt:blacklist:" + jti, "", ttlMillis / 1000, TimeUnit.SECONDS);
    }

    /**
     * Checks whether a token is blacklisted.
     *
     * @param jti UUID of the JWT token. This cannot be {@code null}.
     *
     * @return {@code true} if the token is in the blacklist, otherwise {@code false}.
     */
    public boolean isBlacklisted(String jti) {
        Boolean check = redisTemplate.hasKey("jwt:blacklist:" + jti);
        return Boolean.TRUE.equals(check);
    }
}
