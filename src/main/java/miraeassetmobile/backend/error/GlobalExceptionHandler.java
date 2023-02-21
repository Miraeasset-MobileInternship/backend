package miraeassetmobile.backend.error;
import static miraeassetmobile.backend.error.exception.ErrorCode.SERVER_ERROR;


import lombok.extern.slf4j.Slf4j;

import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.error.exception.UnavailableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice // 모든 RestController error handling
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UnavailableException.class })
    public ResponseEntity<ErrorResponse> handleUnavailableException(UnavailableException ue) {
        log.error("handleUnavailableException throw Exception : {}", ue.getErrorCode());
        return ErrorResponse.toResponseEntity(ue.getErrorCode());
    }

    @ExceptionHandler(value = {NotExistException.class })
    public ResponseEntity<ErrorResponse> handleNotExistException(NotExistException ue) {
        log.error("handleNotExistException throw Exception : {}", ue.getErrorCode());
        return ErrorResponse.toResponseEntity(ue.getErrorCode());
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ErrorResponse> handleException() {
        log.error("Exception throw Exception : {}", SERVER_ERROR.getDetail());
        return ErrorResponse.toResponseEntity(SERVER_ERROR);
    }

}