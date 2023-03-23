package miraeassetmobile.backend.domain.dto.stocks;


import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.AutoComplete;

import java.util.List;

@Builder
@Getter
public class AutoCompleteResponseDto {

    int totalData;

    List<AutoComplete> autoCompleteList;


}

//package miraeassetmobile.backend.domain.dto.stocks;
//
//
//import lombok.Builder;
//import lombok.Getter;
//
//@Builder
//@Getter
//public class AutoCompleteResponseDto {
//
//
//    String stockId; //symbol
//
//    String stockTitle;//주식이름
//
//    TagInfo tagInfo;
//
//
//}

