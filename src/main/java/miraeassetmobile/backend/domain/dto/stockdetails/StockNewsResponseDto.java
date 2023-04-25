package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StockNewsResponseDto {
    int totalData;

    List<StockNewsDto> stockNewsList;

}
