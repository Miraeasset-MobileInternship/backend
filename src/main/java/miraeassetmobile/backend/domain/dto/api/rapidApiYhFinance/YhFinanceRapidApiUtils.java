package miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class YhFinanceRapidApiUtils {

    @Value("${api.rapid-api-yahoo-finance.key}")
    private String apiKey;

    @Value("${api.rapid-api-yahoo-finance.url}")
    private String baseUrl;

    @Value("${api.rapid-api-yahoo-finance.host}")
    private String host;

}
