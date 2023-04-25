package miraeassetmobile.backend.domain.dto.auth.token;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = CacheKey.RefreshToken)
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;


    @TimeToLive
    private Long expiration;


}
