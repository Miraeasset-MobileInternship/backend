package miraeassetmobile.backend.config.security.jwt;

import lombok.RequiredArgsConstructor;

import miraeassetmobile.backend.repository.redis.LogoutAccessTokenRedisRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";  // http header 종류
    public static final String BEARER_PREFIX = "Bearer";    // http 인증 type
    private final TokenProvider tokenProvider;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    // 실제 필터링 로직은 doFilterInternal
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Request Header 에서 access token 추출
        String accessToken = resolveToken(request);;

        // 2. validateToken 으로 토큰 유효성 검사
        if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)){
            // 3. Logout한 회원인지 검사
            checkLogout(accessToken);

            Authentication authentication = tokenProvider.getAuthentication(accessToken); // 토큰으로 증명 가져오기 (authentication)
            SecurityContextHolder.getContext().setAuthentication(authentication); //security context에 저장
        }

        filterChain.doFilter(request,response);
    }

    // Request Header에서 토큰 정보 가져오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);


        if (StringUtils.hasText(bearerToken) &&
                bearerToken.startsWith(BEARER_PREFIX))
            return bearerToken.substring(7); //bearer 제거하고 나머지
        return null;
    }


    // logout인 회원인 경우에는 기존의 access token 접근을 금지시키기
    private void checkLogout(String accessToken) {
        // logoutToken은 해당 회원의 access token을 id로 (unqiue해야함)
        if (logoutAccessTokenRedisRepository.existsById(accessToken)){
            throw new IllegalArgumentException("이미 로그아웃한 회원입니다. 다시 로그인을 진행해주세요.");
        }
    }
}