package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StockPriceGraphDataResponseDto {


    String symbol;

    String period;

    DateInfo dateInfo;

    PriceInfo priceInfo;

    List<PriceData> data;


}
