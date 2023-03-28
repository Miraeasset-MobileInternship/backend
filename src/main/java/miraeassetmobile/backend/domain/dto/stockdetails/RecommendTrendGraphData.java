package miraeassetmobile.backend.domain.dto.stockdetails;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendTrendGraphData {

    String id;
    String label;
    int value;
    String color; //hsl(124, 70%, 50%) 형식

}
