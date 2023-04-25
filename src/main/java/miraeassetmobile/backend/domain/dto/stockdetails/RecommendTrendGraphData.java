package miraeassetmobile.backend.domain.dto.stockdetails;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendTrendGraphData {

    String id;
    int value;
    String color;


}
