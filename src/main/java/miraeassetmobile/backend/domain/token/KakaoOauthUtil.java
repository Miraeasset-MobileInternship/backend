package miraeassetmobile.backend.domain.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoOauthUtil {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUrl;



    //url모음

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authUri;


    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;




}
