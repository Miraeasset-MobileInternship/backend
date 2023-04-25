package miraeassetmobile.backend.domain.dto.classes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@AllArgsConstructor
@Builder
@Getter
@Data
@RedisHash(value = CacheKey.ClassInvitationCode)
public class ClassInvitationCode {


    @Id
    private String id; //학급 아이디

    @Indexed
    private String invitationCode;


    @TimeToLive
    private Long expiration;


}
