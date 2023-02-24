package miraeassetmobile.backend.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@AllArgsConstructor
public enum ErrorCode {


    //권한이 없는 요청의 경우 (403경우)
    //403 : FORBBIDEN : 클라이언트는 접근 권리를 가지고 있지 않습니다. (클라이언트를 알고 있음)
    //401 : UNAUTHORIZED : 클라이언트는 접근 권리를 가지고 있지 않습니다. (클라이언트를 모르기 때문에 접근권한이 없음)
    UNAVAILABLE_ACTION_DELETE_JOB(HttpStatus.FORBIDDEN, "삭제 불가능한 직업입니다."),
    UNAVAILABLE_ACTION_TRANSFER_MONEY(HttpStatus.FORBIDDEN, "학생 잔고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_PAY_MONEY(HttpStatus.FORBIDDEN, "국고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_JOB_TRANSFER(HttpStatus.FORBIDDEN, "학생 잔고를 이체할 수 있는 권한이 없는 직업입니다."),
    UNAVAILABLE_ACTION_JOB_PAY(HttpStatus.FORBIDDEN, "국고를 출금할 수 있는 권한이 없는 직업입니다."),



    //존재하지 않는 리소스에 대한요청
    NOT_EXIST_CLASS(HttpStatus.NOT_FOUND,"존재하지 않는 학급입니다."),
    NOT_EXIST_JOB(HttpStatus.NOT_FOUND,"존재하지 않는 직업입니다."),
    NOT_EXIST_STUDENT(HttpStatus.NOT_FOUND,"존재하지 않는 학생입니다."),



    //이미 존재하는 것을 등록하려고 할 때
    // 같은 학급에 같은 직업 등록 , 회원가입 중복
    ALREADY_EXIST_JOB(HttpStatus.FORBIDDEN, "이미 존재하는 직업입니다."),


    //common error
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 에러가 발생했습니다.")

    ;


    private final HttpStatus httpStatus;

    private final String detail;


}
