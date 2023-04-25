package miraeassetmobile.backend.domain.dto.auth.sms;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@AllArgsConstructor
@Getter
@RedisHash(value = CacheKey.PhoneNumberCode)
@Builder
public class PhoneNumberCode {

    @Id
    private String id; //phonenumber

    private String code; //해당 폰 넘버에 부여된 코드


    @TimeToLive
    private Long expiration; //만료시간


}
