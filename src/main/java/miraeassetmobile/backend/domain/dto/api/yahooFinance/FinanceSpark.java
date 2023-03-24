package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class FinanceSpark {

    String symbol;

    @Nullable
    double previousClose;

    @Nullable
    double chartPreviousClose;

    @Nullable
    List<Long> timestamp;
    @Nullable
    List<Double> close;
    @Nullable
    Long end;
    @Nullable
    Long start;
    @Nullable
    int dataGranularity;


}
