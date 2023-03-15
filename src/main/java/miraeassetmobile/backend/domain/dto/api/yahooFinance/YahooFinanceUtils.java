package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class YahooFinanceUtils {

    @Value("${api.yahoo-finance.key}")
    private String apiKey;

    @Value("${api.yahoo-finance.yh-url}")
    private String baseUrl;

    @Value("${api.yahoo-finance.sa-url}")
    private String alphaUrl;

}
