package miraeassetmobile.backend.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExternalErrorException extends RuntimeException{

    private final ErrorCode errorCode;

}
