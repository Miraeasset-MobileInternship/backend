package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.entity.StockTradingData;

@Getter
@Builder
public class StockBuyingRequestDto {

    Long studentId; //누가 주식을 사려고 하는가

    String stockId; //어떤 주식을 사려고

    int amount; //몇개나 사려고 하는가

    int price; // 얼마에 사려고 하는가 (프론트에 이미 보내줬으니까)



    public StockTradingData toStockTradingData(Long studentId, String stockId, int amount, int price){
        return StockTradingData.builder()
                .studentId(studentId)
                .stockSymbol(stockId)
                .amount(amount)
                .price(price)
                .buying(true) //buying request이므로 무조건 true
                .build();
    }

}
