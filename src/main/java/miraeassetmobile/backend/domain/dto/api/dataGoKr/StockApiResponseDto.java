package miraeassetmobile.backend.domain.dto.api.dataGoKr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockApiResponseDto {


    private Integer pageNo;


    private Integer totalCount;


    private Integer numOfRows;

    private List<Item> items;



}
