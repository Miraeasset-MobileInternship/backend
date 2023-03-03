package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TotalStockInfoResponseDto {

    Long studentId;

    int money;

    int totalMarketValue; //평가금액

    int totalBlendedPrice; //매수금액


    int totalMarketProfitLoss; //평가손익

    String totalYield; //수익률 (2자리수 끊어서 보내기)


}
