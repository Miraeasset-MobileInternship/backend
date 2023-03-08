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

    private final TokenProvider tokenProvider;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("filterchain");

        // 1. Request Header 에서 access token 빼기
        String accessToken = tokenProvider.resolveToken(request);
        System.out.println(accessToken);
        System.out.println("filterchain");
        // 2. validateToken 으로 토큰 유효성 검사
        if (accessToken!=null && StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
            // 3. Logout한 회원인지 검사
            checkLogout(accessToken);
            // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        System.out.println("filterchain");
        filterChain.doFilter(request, response);
    }

    // logout인 회원일 경우 해당 회원의 access token으로 접근을 방지하기 위함
    private void checkLogout(String accessToken) {
        // logoutToken은 해당 회원의 access token을 id로 가짐.
        if (logoutAccessTokenRedisRepository.existsById(accessToken)) {
            throw new IllegalArgumentException("이미 로그아웃한 회원입니다.");
        }
    }


}
