package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SimilarStockResponseDto {

    int totalData; //총 데이터의 수(list의 길이)

    String stockTitle; //유사 기준 종목명

    List<SimilarStockInfo> stockInfoList;

}
