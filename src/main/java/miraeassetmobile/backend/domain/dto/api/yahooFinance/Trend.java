package miraeassetmobile.backend.domain.dto.api.yahooFinance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trend {

    String period;
    int strongBuy;

    int buy;

    int hold;

    int sell;

    int strongSell;

}
