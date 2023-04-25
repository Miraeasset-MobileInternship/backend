package miraeassetmobile.backend.repository.redis;


import miraeassetmobile.backend.domain.entity.error.SearchQueryLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SearchQueryLogRedisRepository extends CrudRepository<SearchQueryLog, String> {

    @Override
    List<SearchQueryLog> findAll();

}
