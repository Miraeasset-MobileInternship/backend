package miraeassetmobile.backend.domain.dto.api.yahooFinance;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class SimilarSymbol {

    double score;
    String symbol;


}
