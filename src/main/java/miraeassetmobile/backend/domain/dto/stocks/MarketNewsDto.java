package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MarketNewsDto {

    String link;

    String title;

    String date;


//    String time;


    String source;



}
