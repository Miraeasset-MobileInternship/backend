package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.token.KakaoOauthUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class AuthService {

    KakaoOauthUtil kakaoOauthUtil;


    AuthService(KakaoOauthUtil kakaoOauthUtil){
        this.kakaoOauthUtil =kakaoOauthUtil;
    }

    public ResponseEntity redirectToKakaoLoginPage(String callBackUrl) throws URISyntaxException {
        URI redirectUri = new URI(kakaoOauthUtil.getAuthUri()+"?client_id="+kakaoOauthUtil.getClientId()+"&redirect_uri="+callBackUrl+"&response_type=code");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }


    public String getKakaoTokenWithCode(String code){
        return code;
    }

}
