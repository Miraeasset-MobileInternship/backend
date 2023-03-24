package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TrendingEvery;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface TrendingEveryRepository extends JpaRepository<TrendingEvery,Long> {

    Optional<TrendingEvery> findTop1ByOrderByCreateTimestampDesc();

}
