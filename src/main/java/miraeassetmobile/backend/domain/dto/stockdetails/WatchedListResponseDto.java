package miraeassetmobile.backend.domain.dto.stockdetails;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WatchedListResponseDto {

    int totalData;

    List<WatchedStockInfo> watchedStockInfoList;

}
