package miraeassetmobile.backend.repository.redis;


import miraeassetmobile.backend.domain.dto.classes.ClassInvitationCode;
import org.springframework.data.repository.CrudRepository;

public interface ClassInvitationCodeRedisRepository extends CrudRepository<ClassInvitationCode, String> {
}
