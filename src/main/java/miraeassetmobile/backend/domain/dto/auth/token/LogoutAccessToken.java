package miraeassetmobile.backend.domain.dto.auth.token;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@AllArgsConstructor
@Getter
//@RedisHash(value = "logoutAccessToken")
@RedisHash(value = CacheKey.LogoutAccessToken)
@Builder
public class LogoutAccessToken {

    @Id
    private String id; //accessToken : 유니크해야하는데 이름이 id만 가능..

    private Long userId; // userId

    @TimeToLive
    private Long expiration;

    public static LogoutAccessToken of(String accessToken, Long userId, Long remainExpirationMillSecond) {
        return LogoutAccessToken.builder()
                .id(accessToken)
                .userId(userId)
                .expiration(remainExpirationMillSecond)
                .build();
    }

}
