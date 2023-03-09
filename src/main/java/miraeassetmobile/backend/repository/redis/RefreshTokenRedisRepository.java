package miraeassetmobile.backend.repository.redis;

import miraeassetmobile.backend.domain.dto.auth.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}
