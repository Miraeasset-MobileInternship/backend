package miraeassetmobile.backend.domain.dto.stocks;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckForSellingStockResponseDto {

    String stockId;//심볼

    String stockTitle;//종목명

    String marketPrice; //현재가

    int price;//실거래가

    int availableAmount;//판매가능 수량

    String currency;


}
