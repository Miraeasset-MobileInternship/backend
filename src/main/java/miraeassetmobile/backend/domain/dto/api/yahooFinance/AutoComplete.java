package miraeassetmobile.backend.domain.dto.api.yahooFinance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class AutoComplete {

    String exch;
    String exchDisp;
    String name;
    String symbol;
    String type;
    String typeDisp;

}
