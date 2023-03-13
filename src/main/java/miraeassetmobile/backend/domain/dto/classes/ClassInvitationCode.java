package miraeassetmobile.backend.domain.dto.classes;

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
@RedisHash(value = CacheKey.ClassInvitationCode)
public class ClassInvitationCode {


    @Id
    private String id; //학급 아이디

    private String invitationCode;


    @TimeToLive
    private Long expiration;


}
