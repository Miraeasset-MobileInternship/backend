package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnStockInfo {

    String stockId;//symbol

    //displayName
    String stockTitle; //display name (종목명)

    //regularMarketPrice
    String price; //실시간 금액 또는 종가(종목 현재가)


    int count; //보유 수량

    String blendedPrice; // 평균구매단가

    String marketPrice;// 평가금액
    String marketProfitLoss; //평가손익

    String yield; //수익률 (2자리수 끊어서 보내기)

    int yieldStatus; // 1: + / 0: 0 / -1 : -

    //typeDisp
    TagInfo tagInfo;

}
