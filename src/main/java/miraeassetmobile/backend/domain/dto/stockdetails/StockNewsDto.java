package miraeassetmobile.backend.domain.dto.stockdetails;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockNewsDto {

    String link;

    String title;

    String date;


}
