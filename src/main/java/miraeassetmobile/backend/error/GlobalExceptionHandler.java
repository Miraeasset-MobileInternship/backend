package miraeassetmobile.backend.error;


import lombok.extern.slf4j.Slf4j;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.error.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

import static miraeassetmobile.backend.error.exception.ErrorCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice // 모든 RestController error handling
//Filter(JWT), Interceptor 단에서 발생하는 Exception을 처리할 수 없어 JWT Exception에는 부적합
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    //v2
    @ExceptionHandler(value = {ServiceException.class })
    public ResponseEntity<BanklassResponseEntity> handleException(ServiceException ue) {
        log.error("Exception : ", ue.getErrorCode().getDetail());

        return ResponseEntity.ok(
                BanklassResponseEntity.builder()
                        .status(
                                StatusResponse.builder()
                                        .status(ue.getErrorCode().getStatus())
                                        .message(ue.getErrorCode().getDetail())
                                        .build()
                        )
                        .result(
                                null
                        )
                        .build()
        );
    }




    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<BanklassResponseEntity> handleException() {
        log.error("Exception throw Exception : {}", SERVER_ERROR.getDetail());
//        return ErrorResponse.toResponseEntity(SERVER_ERROR);

        return ResponseEntity.ok(
                BanklassResponseEntity.builder()
                        .status(
                                StatusResponse.builder()
                                        .status("E500")
                                        .message("서버에러발생")
                                        .build()
                        )
                        .result(
                                new ArrayList<>()
                        )
                        .build()
        );

    }


}