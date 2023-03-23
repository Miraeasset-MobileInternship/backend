package miraeassetmobile.backend.domain.dto.api.naver;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NaverApiServiceUtils {

    @Value("${api.naver-translator.client-secret}")
    private String clientSecret;

    @Value("${api.naver-translator.url}")
    private String baseUrl;

    @Value("${api.naver-translator.client-id}")
    private String clientId;



}
