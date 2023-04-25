package miraeassetmobile.backend.domain.dto.stockdetails;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WatchedStockInfo {

    String symbol;

    String stockTitle;

    double price;

    double changePercent;

    double changePrice;

    int rank;


}
