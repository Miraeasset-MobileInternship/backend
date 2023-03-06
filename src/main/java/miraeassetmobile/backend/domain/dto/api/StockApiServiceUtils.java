package miraeassetmobile.backend.domain.dto.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class StockApiServiceUtils {

    @Value("${api.key}")
    private String secretKey;

    @Value("${api.url}")
    private String hostUrl;
}
