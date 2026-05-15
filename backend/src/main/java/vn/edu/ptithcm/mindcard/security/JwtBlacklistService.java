package vn.edu.ptithcm.mindcard.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * Add jwt token to blacklist.
     * If {@code jti} or {@code expiration} is null this function will return and do not thing.
     * If token is expired, this function will return and do not thing.
     * @param jti UUID of JWT token
     * @param expiration expiration date
     */
    public void addToBlackList(String jti, Date expiration){
        if (jti == null || expiration == null){
            return;
        }

        Date now = new Date();
        if (now.after(expiration)){
            return;
        }
        long ttlMillis = expiration.getTime() - now.getTime();
        redisTemplate.opsForValue().set("jwt:blacklist:" + jti, "", ttlMillis/1000, TimeUnit.SECONDS);
    }

    /**
     * Check if token is in blacklist or not
     * @param jti UUID of JWT token. This cannot be {@code null}
     * @return {@code true} if token in blacklist else {@code false}
     */
    public boolean isBlacklisted(String jti){
        Boolean check = redisTemplate.hasKey("jwt:blacklist:" + jti);
        return Boolean.TRUE.equals(check);
    }
}
