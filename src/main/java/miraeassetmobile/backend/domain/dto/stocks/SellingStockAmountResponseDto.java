package miraeassetmobile.backend.domain.dto.stocks;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellingStockAmountResponseDto {

    String stockId;

    int amount;


}
