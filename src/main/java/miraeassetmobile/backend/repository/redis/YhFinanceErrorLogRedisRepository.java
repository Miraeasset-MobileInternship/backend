package miraeassetmobile.backend.repository.redis;


import miraeassetmobile.backend.domain.entity.error.YhFinanceErrorLog;
import org.springframework.data.repository.CrudRepository;

public interface YhFinanceErrorLogRedisRepository extends CrudRepository<YhFinanceErrorLog,String> {
}
