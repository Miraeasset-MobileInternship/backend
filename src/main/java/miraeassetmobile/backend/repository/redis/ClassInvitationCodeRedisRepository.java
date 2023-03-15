package miraeassetmobile.backend.repository.redis;


import miraeassetmobile.backend.domain.dto.classes.ClassInvitationCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClassInvitationCodeRedisRepository extends CrudRepository<ClassInvitationCode, String> {

    Optional<ClassInvitationCode> findByInvitationCode(String invitationCode);


}
