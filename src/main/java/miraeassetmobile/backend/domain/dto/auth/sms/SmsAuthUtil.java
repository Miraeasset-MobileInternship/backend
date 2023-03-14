package miraeassetmobile.backend.domain.dto.auth.sms;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SmsAuthUtil {


    @Value("${coolsms.api-key}")
    private String apiKey;


    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from-number}")
    private String fromNumber;


    private long expiration = 60 * 3;

}
