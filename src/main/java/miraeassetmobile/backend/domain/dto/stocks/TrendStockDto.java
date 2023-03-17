package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrendStockDto {

    //symbol
    String id; // ID값 같은것 (검색 할때 필수적으로 필요)

    //displayName
    String stockTitle; //display name (종목명)

    //regularMarketPrice
    String price; //실시간 금액 또는 종가


    //regularMarketChange
    String change;//변동가


    //regularMarketChangePercent
    String changePercent; // 변동 퍼센트


    //typeDisp
    String type;

    //fullExchangeName
    String market; //상장된 시장 (fullExchangeName)

    //customPriceAlertConfidence
    String customPriceConfidence;

    boolean isOpen; //장의 상태(open/closed)

}
