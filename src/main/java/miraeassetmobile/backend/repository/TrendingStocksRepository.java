package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TrendingStocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TrendingStocksRepository extends JpaRepository<TrendingStocks,Long> {

    Optional<TrendingStocks> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);

    @Query(value = "SELECT * FROM trending_stocks ORDER BY create_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<TrendingStocks> findLastUpdate();

    @Query(value = "SELECT * FROM trending_stocks WHERE create_timestamp =:createTimeStamp", nativeQuery = true)
    List<TrendingStocks> findLastestUpdatedList(Timestamp createTimeStamp);

}
