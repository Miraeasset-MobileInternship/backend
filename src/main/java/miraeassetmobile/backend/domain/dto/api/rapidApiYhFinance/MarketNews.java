package miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class MarketNews {

    String title;

    String link;

    String pubDate;

    String source;

    String guid;

}
