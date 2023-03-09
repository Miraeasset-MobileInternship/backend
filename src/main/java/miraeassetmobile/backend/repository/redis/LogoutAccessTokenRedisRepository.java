package miraeassetmobile.backend.repository.redis;

import miraeassetmobile.backend.domain.dto.auth.token.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessToken, String> {
}
