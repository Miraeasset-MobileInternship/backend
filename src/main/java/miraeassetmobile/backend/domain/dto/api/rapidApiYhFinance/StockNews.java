package miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
public class StockNews {

    String description;
    String guid;
    String link;
    String pubDate;
    String title;

}
