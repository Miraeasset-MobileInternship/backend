package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SimilarStockInfo {

    String symbol;

    String stockTitle;

    double price;

    double changePrice;

    double changePercent;

}
