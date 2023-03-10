package miraeassetmobile.backend.repository.redis;

import miraeassetmobile.backend.domain.dto.auth.sms.PhoneNumberCode;
import org.springframework.data.repository.CrudRepository;

public interface PhoneNumberCodeRedisRepository extends CrudRepository<PhoneNumberCode, String> {
}
