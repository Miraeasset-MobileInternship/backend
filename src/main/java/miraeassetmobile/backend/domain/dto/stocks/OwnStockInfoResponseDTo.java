package miraeassetmobile.backend.domain.dto.stocks;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OwnStockInfoResponseDTo {

    int currentPage;

    int maxPage;

    int totalData;


    String classCurrency; //학급 화폐단위

    List<OwnStockInfo> stockInfoList;



}
