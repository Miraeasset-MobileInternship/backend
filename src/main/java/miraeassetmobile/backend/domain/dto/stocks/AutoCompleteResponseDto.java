package miraeassetmobile.backend.domain.dto.stocks;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AutoCompleteResponseDto {


    String stockId; //symbol

    String stockTitle;//주식이름

    TagInfo tagInfo;


}
