package miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class StockNewsTime {

    String description;
    String guid;
    String link;
    Long pubDate;
    String title;


}
