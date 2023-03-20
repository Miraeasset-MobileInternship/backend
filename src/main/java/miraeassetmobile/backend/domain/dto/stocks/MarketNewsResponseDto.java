package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MarketNewsResponseDto {

    String link;

    String title;

    String date;


//    String time;


    String source;



}
