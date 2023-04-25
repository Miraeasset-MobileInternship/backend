package miraeassetmobile.backend.domain.dto.stockdetails;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RecommendTrendGraphResponseDto {


    String recommendStatus; // 추천 상태 현황

    List<RecommendTrendGraphData> graphData;


}
