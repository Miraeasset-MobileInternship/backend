package miraeassetmobile.backend.config.security.jwt;

import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.UnavailableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException){

        throw new UnavailableException(ErrorCode.FORBBIDEN_USER); //사용할 수 없는 사용자 에러

//        response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403에러
    }


}
