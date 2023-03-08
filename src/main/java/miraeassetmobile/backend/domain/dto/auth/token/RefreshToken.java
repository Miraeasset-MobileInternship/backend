package miraeassetmobile.backend.domain.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.Date;


@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = CacheKey.RefreshToken)
public class RefreshToken {

    @Id
    private String id; // userId라는 이름으로 아이디를 지을 수 없다고 함..
    private String refreshToken;

    // redis에서 설정한 시간 이후에 자동으로 해당 데이터가 사라지는 휘발성 데이터를 만들어주는 annotation
    @TimeToLive
    private Long expiration;



}
