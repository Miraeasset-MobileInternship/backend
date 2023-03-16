package miraeassetmobile.backend.error;


import lombok.extern.slf4j.Slf4j;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.error.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@Slf4j
@RestControllerAdvice // 모든 RestController error handling
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


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
                                new ArrayList<>()
                        )
                        .build()
        );
    }

//    @ExceptionHandler(value = {NotExistException.class })
//    public ResponseEntity<ErrorResponse> handleNotExistException(NotExistException ue) {
//        log.error("handleNotExistException throw Exception : {}", ue.getErrorCode());
//        return ErrorResponse.toResponseEntity(ue.getErrorCode());
//    }
//
//
//    @ExceptionHandler(value = {AlreadyExistException.class })
//    public ResponseEntity<ErrorResponse> handleAlreadyExistException(AlreadyExistException ue) {
//        log.error("handleAlreadyExistException throw Exception : {}", ue.getErrorCode());
//        return ErrorResponse.toResponseEntity(ue.getErrorCode());
//    }
//
//
//    @ExceptionHandler(value = {ExternalErrorException.class })
//    public ResponseEntity<ErrorResponse> handleExternalErrorException(ExternalErrorException ue) {
//        log.error("handleExternalErrorException throw Exception : {}", ue.getErrorCode());
//        return ErrorResponse.toResponseEntity(ue.getErrorCode());
//    }
//
//
//    @ExceptionHandler(value = {CustomLoginException.class })
//    public ResponseEntity<ErrorResponse> handleCustomLoginException(CustomLoginException ue) {
//        log.error("handleExternalErrorException throw Exception : {}", ue.getErrorCode());
//        return ErrorResponse.toResponseEntity(ue.getErrorCode());
//    }


//    @ExceptionHandler(value = { Exception.class })
//    public ResponseEntity<ErrorResponse> handleException() {
//        log.error("Exception throw Exception : {}", SERVER_ERROR.getDetail());
//        return ErrorResponse.toResponseEntity(SERVER_ERROR);
//    }

}