package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.StockTradingData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTradingDataRepository extends JpaRepository<StockTradingData, Long> {
}
