package miraeassetmobile.backend.domain.entity.error;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = CacheKey.YhFinanceErrorLog)
public class YhFinanceErrorLog {

    @Id
    private String id;

    private String errorMessage;


    @CreatedDate
    private Timestamp timestamp;


}
