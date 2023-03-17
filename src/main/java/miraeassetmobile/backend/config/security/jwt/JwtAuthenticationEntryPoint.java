package miraeassetmobile.backend.config.security.jwt;


import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 유효 자격증명을 제공하지 않고 접근할 경우 401
        setResponse(response, ErrorCode.UNAUTHORIZED_USER);
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