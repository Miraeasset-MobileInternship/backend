package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MarketNewsResponseDto {


    int totalData;

    List<MarketNewsDto> marketNewsList;

}
