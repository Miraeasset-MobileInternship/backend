package miraeassetmobile.backend.domain.dto.stocks;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockPriceResponseDto {

    String stockId;


    String marketPrice;

    int tradingPrice;



}
