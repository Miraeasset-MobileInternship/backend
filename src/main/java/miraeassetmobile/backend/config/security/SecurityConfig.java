package miraeassetmobile.backend.config.security;

import lombok.RequiredArgsConstructor;
import miraeassetmobile.backend.config.security.jwt.JwtAccessDeniedHandler;
import miraeassetmobile.backend.config.security.jwt.JwtAuthenticationEntryPoint;
import miraeassetmobile.backend.config.security.jwt.TokenProvider;
import miraeassetmobile.backend.repository.redis.LogoutAccessTokenRedisRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //Security 관련 설정을 활성화 -> 기본 스프링 filterChain에 등록한다
public class SecurityConfig extends WebSecurityConfigurerAdapter {



    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;


    //AUTH 없이 사용할 수 있는 URL목록
    private static final String[] AUTH_LIST = {
            "/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/api/**",

    };

    @Override
    public void configure(WebSecurity web){
        // ACL(access control list)에 url 추가
        web.ignoring().antMatchers("/swagger-ui/**",
                "/swagger-resources/**", "/v3/api-docs/**");
    }

    protected void configure(HttpSecurity http) throws Exception {

        // csrf로 인한 forbidden error 방지

        http.csrf().disable()//REST API 서버 CSRF처리 해제
                .exceptionHandling()//예외처리 수행
//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // Custom Jwt 토큰 필터가 있는경우
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)//인증(사용자 신원 검증) 실패시
                .accessDeniedHandler(jwtAccessDeniedHandler)//인가(인증 후에 자원 접근 프로세스) 실패시

                // 시큐리티는 기본적으로 세션을 통해 유저 정보들을 저장
                // 하지만 redis를 사용하므로 세션을 사용하지 않아 세션 설정을 Stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 인증없이 접근 가능한 url을 설정, AUTH_LIST에 추가하면 사용 가능.
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_LIST).permitAll()
                .anyRequest().authenticated()





                .and()
                .apply(new JwtSecurityConfig(tokenProvider, logoutAccessTokenRedisRepository));

    }



}
