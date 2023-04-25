package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockInfoResponseDto {

    String exchangeName; //거래소명 (나스닥 등)


    /*
    52주 최저/최고/변동
     */
    double fiftyTwoWeekHigh;
    double fiftyTwoWeekLow;
    double fiftyTwoWeekHighChange;
    double fiftyTwoWeekLowChange;

    //eps , per
    double epsCurrentYear;
    String typeDisp; //type
    String region; //US
    String financialCurrency;
    Long averageDailyVolume10Day;
    Long averageDailyVolume3Month;



}
