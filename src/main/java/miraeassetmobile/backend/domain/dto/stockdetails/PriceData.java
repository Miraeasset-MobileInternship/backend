package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PriceData {


    Long time;
    double price;


}
