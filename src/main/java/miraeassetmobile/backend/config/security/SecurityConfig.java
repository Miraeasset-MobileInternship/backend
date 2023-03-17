package miraeassetmobile.backend.config.security;

import lombok.RequiredArgsConstructor;
import miraeassetmobile.backend.config.security.jwt.JwtAuthenticationEntryPoint;
import miraeassetmobile.backend.repository.redis.LogoutAccessTokenRedisRepository;
import miraeassetmobile.backend.config.security.jwt.JwtAccessDeniedHandler;
import miraeassetmobile.backend.config.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    private static final String[] AUTH_LIST = {
            "/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/api/auth/**",
            "/api/**",//개발단계 편의를 위해 임시 추가
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ACL(access control list)에 url 추가
        web.ignoring().antMatchers("/book/**"); // 문제 조회 test를 위해 추가
        web.ignoring().antMatchers("/swagger-ui/**",
                "/swagger-resources/**", "/v3/api-docs/**"); // debug시 swagger 사용을 위해 추가
    }

    protected void configure(HttpSecurity http) throws Exception {

        // csrf로 인한 forbidden error 방지
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 시큐리티는 기본적으로 세션을 통해 유저 정보들을 저장
                // 하지만 redis를 사용하므로 세션을 사용하지 않아 세션 설정을 Stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 인증없이 접근 가능한 url을 설정, AUTH_LIST에 추가하면 사용 가능.
                .and()
                .authorizeRequests()
//                .antMatchers("/api/auth").permitAll()
                .antMatchers(AUTH_LIST).permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider, logoutAccessTokenRedisRepository));
    }
}