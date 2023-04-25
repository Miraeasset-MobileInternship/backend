package miraeassetmobile.backend.repository;


import miraeassetmobile.backend.domain.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockBatchRepository extends JpaRepository<StockBatch,Long> {


    Optional<StockBatch> findByStockSymbol(String stockSymbol);

    boolean existsByStockSymbol(String stockSymbol);

}
