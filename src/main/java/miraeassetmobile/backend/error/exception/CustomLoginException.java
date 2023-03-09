package miraeassetmobile.backend.error.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomLoginException extends RuntimeException{

    private final ErrorCode errorCode;

}
