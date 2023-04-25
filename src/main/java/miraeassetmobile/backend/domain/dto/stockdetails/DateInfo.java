package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DateInfo {

    Long maxDate;
    Long minDate;


}
