package miraeassetmobile.backend.error.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class JwtCustomException extends JwtException {
    private final ErrorCode errorCode;

    public JwtCustomException(String message,ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
