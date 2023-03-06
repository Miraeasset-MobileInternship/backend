package miraeassetmobile.backend.repository.redis;

import miraeassetmobile.backend.domain.token.TeacherRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface TeacherRefreshTokenRedisRepository extends CrudRepository<TeacherRefreshToken, String> {
}
