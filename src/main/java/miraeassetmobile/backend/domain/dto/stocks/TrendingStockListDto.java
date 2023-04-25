package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrendingStockListDto {

    int totalNum; // 총 인기 종목의 수

    List<TrendStockDto> trendStockList;



}
