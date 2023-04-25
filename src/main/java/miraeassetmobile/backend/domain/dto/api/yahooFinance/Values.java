package miraeassetmobile.backend.domain.dto.api.yahooFinance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class Values {

    int raw;
    String fmt;
    String longFmt;

}
