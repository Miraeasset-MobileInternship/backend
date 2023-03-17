package miraeassetmobile.backend.config.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import miraeassetmobile.backend.domain.dto.auth.token.TokenDto;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.JwtCustomException;
import miraeassetmobile.backend.error.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

// 실제 인증에 대한 부분 중 인증 전 객체를 받아 인증된 객체를 반환하는 역할
@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";     // token 인증 타입(jwt 토큰을 의미)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 24;       // 1일
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 7;  // 7일

    // HS512알고리즘
    //
    private final Key key;
    public TokenProvider(@Value("${jwt.key}") String secretKey
    ){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Authentication authentication){

        // 권한 가져오기
        String auth = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        long now = (new Date()).getTime();

        // access token
        Date accessTokenExpires = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, auth)
                .setExpiration(accessTokenExpires)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();



        // refresh token (만료일자만 저장)
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();



        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpires.getTime())
                .refreshToken(refreshToken)
                .userId(
                        Long.parseLong(authentication.getName())
                )
                .build();
    }

    public Authentication getAuthentication(String accessToken){

        // 토큰 복호화 (내부 정보 가져옴)
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null){
            throw new ServiceException(ErrorCode.UNAUTHORIZED_TOKEN);
        }

        // 권한 정보 가져옴
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString()
                                .split(",")).map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "",
                authorities);

        return new UsernamePasswordAuthenticationToken(principal,
                "", authorities);
    }

    // 만료된 토큰의 경우에도 정보를 꺼내기 위한 메소드
    private Claims parseClaims(String accessToken){

        try {
            return Jwts.parserBuilder().setSigningKey(key)
                    .build().parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    // 토큰 정보 검증
    public boolean validateToken(String token) {

        /*
        error log:

        IllegalArgumentException 은 정상적으로 serviceException이 작동하지만
        나머지는 breakpoint : null 로 에러였다
        -> 원인 : 나머지 4개는 RuntimeException(serviceException extends RuntimeException)
        이 아니었기 때문..

        우리는 ErrorCode를 사용해야하기 때문에 마찬가지로
        JwtException 을 extend한 class를 하나 설정 한 뒤 해결하였다

        참고로 RuntimeException은 filter, Intercept로 발생한 에러는 잡아내지 못한다


        1. JWTCustomException생성
        2. JWTException이 발생하는 구간 try-catch로 잡기
        3. catch에서 JwtException이 발생하면 catch내에 response생성 함수로 보내기
        4. response 생성 함수를 통해 같은 형식으로 내보내기

         */


        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new JwtCustomException(e.getMessage(), ErrorCode.INVALID_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new JwtCustomException(e.getMessage(),ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new JwtCustomException(e.getMessage(),ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new ServiceException(ErrorCode.TOKEN_NOT_EXIST);
        }
//        return false;
    }


    public static Date getRefreshTokenExpireTime() {
        return new Date(new Date().getTime()+REFRESH_TOKEN_EXPIRE_TIME);
    }


    public long getRefreshTokenRemainExpiration() {
        Date d = getRefreshTokenExpireTime();
        Date now = new Date();


        return ((d.getTime() - now.getTime())/1000)+1;
    }


    // logout token의 expiration 계산
    public long getRemainExpiration(String token){
        Date currentTime = parseClaims(token).getExpiration();
        Date now = new Date();
        // redis의 단위는 초로, 밀리초를 초로 변환하는 과정의 오차를 감안하기 위해 1초 더함.
        return ((currentTime.getTime() - now.getTime())/1000)+1;
    }

    // Request Header에서 토큰 정보 가져오기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER);


        if (StringUtils.hasText(bearerToken) &&
                bearerToken.startsWith(JwtAuthenticationFilter.BEARER_PREFIX))
            return bearerToken.substring(7); //bearer 제거하고 나머지
        return null;
    }




}