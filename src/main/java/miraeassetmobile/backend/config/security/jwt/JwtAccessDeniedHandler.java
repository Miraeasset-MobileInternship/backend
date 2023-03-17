package miraeassetmobile.backend.config.security.jwt;

import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ErrorCode.*;
import miraeassetmobile.backend.error.exception.ServiceException;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 필요 권한 없이 접근할 경우 403
        setResponse(response, ErrorCode.FORBIDDEN_USER);
    }



    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); //무조건 OK로!


        JSONObject result = new JSONObject();
        JSONObject resultStatus = new JSONObject();

        resultStatus.put("timestamp", LocalDateTime.now().toString());
        resultStatus.put("status", errorCode.getStatus());
        resultStatus.put("message", errorCode.getDetail());

        result.put("status", resultStatus);
        result.put("result", new ArrayList<>());


        response.getWriter().print(result); //string으로 swagger가 인식함


// swagger에서 json parsing이 불가능하다고 뜸
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