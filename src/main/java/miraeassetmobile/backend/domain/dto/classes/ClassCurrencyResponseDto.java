package miraeassetmobile.backend.domain.dto.classes;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClassCurrencyResponseDto {

    Long classId;

    String currency;

}
