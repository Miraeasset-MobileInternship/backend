package miraeassetmobile.backend.domain.entity.error;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.config.CacheKey;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@AllArgsConstructor
@Builder
@Getter
@RedisHash(value = CacheKey.SearchQuery)
public class SearchQueryLog {

    @Id
    private String id;

    @Indexed
    private String query;

    private String timestamp;


}
