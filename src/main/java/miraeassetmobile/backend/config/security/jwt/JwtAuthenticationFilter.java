package miraeassetmobile.backend.config.security.jwt;

import lombok.RequiredArgsConstructor;

import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.JwtCustomException;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.redis.LogoutAccessTokenRedisRepository;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

        try {
            // 1. Request Header 에서 access token 추출
            String accessToken = tokenProvider.resolveToken(request);

            // 2. validateToken 으로 토큰 유효성 검사
            if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
                // 3. Logout한 회원인지 검사
                checkLogout(response, accessToken);

                Authentication authentication = tokenProvider.getAuthentication(accessToken); // 토큰으로 증명 가져오기 (authentication)
                SecurityContextHolder.getContext().setAuthentication(authentication); //security context에 저장
            }

            filterChain.doFilter(request, response);
        }catch (JwtCustomException ex){
            setResponse(response, ex.getErrorCode());
        }
    }



    // logout인 회원인 경우에는 기존의 access token 접근을 금지시키기
    private void checkLogout(HttpServletResponse response, String accessToken) throws IOException {
        // logoutToken은 해당 회원의 access token을 id로 (unqiue해야함)
        if (logoutAccessTokenRedisRepository.existsById(accessToken)){
            setResponse(response, ErrorCode.LOGOUT_USER);
        }
    }


    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); //무조건 OK


        JSONObject result = new JSONObject();
        JSONObject resultStatus = new JSONObject();

        resultStatus.put("timestamp", LocalDateTime.now().toString());
        resultStatus.put("status", errorCode.getStatus());
        resultStatus.put("message", errorCode.getDetail());

        result.put("status", resultStatus);
        result.put("result", new ArrayList<>());


        response.getWriter().print(result);


        //        BanklassResponseEntity b = BanklassResponseEntity.builder()
//                .status(
//                        StatusResponse.builder()
//                                .status(errorCode.getStatus())
//                                .message(errorCode.getDetail())
//                                .build()
//                )
//                .result(new ArrayList<>())
//                .build();
//
//        response.getWriter().print(b);


    }


}