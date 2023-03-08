package miraeassetmobile.backend.domain.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@AllArgsConstructor
@Getter
@RedisHash(value = CacheKey.LogoutAccessToken)
@Builder
public class LogoutAccessToken {

    @Id
    private String id; //userId

    private String accessId; //accessToken id

    @TimeToLive
    private Long expiration;

    public static LogoutAccessToken of(String accessToken, String accessId,
                                       Long remainExpirationMillSecond) {
        return LogoutAccessToken.builder()
                .id(accessToken)
                .accessId(accessId)
                .expiration(remainExpirationMillSecond)
                .build();
    }

}
