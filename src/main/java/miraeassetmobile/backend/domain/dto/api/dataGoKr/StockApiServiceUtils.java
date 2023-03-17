package miraeassetmobile.backend.domain.dto.api.dataGoKr;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class StockApiServiceUtils {

    @Value("${api.data-go-kr.key}")
    private String secretKey;

    @Value("${api.data-go-kr.url}")
    private String hostUrl;
}
