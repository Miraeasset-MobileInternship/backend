package miraeassetmobile.backend.domain.dto.stockdetails;



import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.stocks.TagInfo;

@Builder
@Getter
public class StockDetailResponseDto {


    String symbol;

    String stockTitle;

    double price;

    double changePrice;

    double changePercent;


    TagInfo tagInfo;


}
