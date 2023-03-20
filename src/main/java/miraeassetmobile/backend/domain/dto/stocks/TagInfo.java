package miraeassetmobile.backend.domain.dto.stocks;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagInfo {
    //typeDisp
    String type;

    //fullExchangeName
    String market; //상장된 시장 (fullExchangeName)

    //customPriceAlertConfidence
    String customPriceConfidence;

    boolean isOpen; //장의 상태(open/closed)


}
