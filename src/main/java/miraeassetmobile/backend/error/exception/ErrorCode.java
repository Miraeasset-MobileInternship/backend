package miraeassetmobile.backend.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //로그인 (인증/인가 관련에러)
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "서비스를 사용할 수 없는 사용자입니다."),//403
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 입니다."), //401

    //로그인 중 가입안된 사람이라 가입 처리 요청시
    SIGN_UP_REQUIRED(HttpStatus.SEE_OTHER, "회원가입이 필요한 사용자 입니다. 회원가입 창으로 넘어가 진행 후 다시 로그인을 시도해주세요."),

    // 로그인 진행 중 발생하는 필연적인 에러들 프론트에 전달하는 용도
    FORBIDDEN_USER_LOGOUT(HttpStatus.FORBIDDEN, "이미 로그아웃한 유저입니다. 다시 로그인을 진행해주세요."),



    //권한이 없는 요청의 경우 (403경우)
    //403 : FORBIDEN : 클라이언트는 접근 권리를 가지고 있지 않습니다. (클라이언트를 알고 있음)
    //401 : UNAUTHORIZED : 클라이언트는 접근 권리를 가지고 있지 않습니다. (클라이언트를 모르기 때문에 접근권한이 없음)
    UNAVAILABLE_ACTION_DELETE_JOB(HttpStatus.FORBIDDEN, "삭제 불가능한 직업입니다."),
    UNAVAILABLE_ACTION_TRANSFER_MONEY(HttpStatus.FORBIDDEN, "학생 잔고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_PAY_MONEY(HttpStatus.FORBIDDEN, "국고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_JOB_TRANSFER(HttpStatus.FORBIDDEN, "학생 잔고를 이체할 수 있는 권한이 없는 직업입니다."),
    UNAVAILABLE_ACTION_JOB_PAY(HttpStatus.FORBIDDEN, "국고를 출금할 수 있는 권한이 없는 직업입니다."),
    UNAVAILABLE_ACTION_TOO_MANY_JOBS(HttpStatus.FORBIDDEN, "학급 당 직업은 50개를 초과할 수 없습니다."),


    //존재하지 않는 리소스에 대한요청
    NOT_EXIST_CLASS(HttpStatus.NOT_FOUND,"존재하지 않는 학급입니다."),
    NOT_EXIST_JOB(HttpStatus.NOT_FOUND,"존재하지 않는 직업입니다."),
    NOT_EXIST_STUDENT(HttpStatus.NOT_FOUND,"존재하지 않는 학생입니다."),



    //이미 존재하는 것을 등록하려고 할 때
    // 같은 학급에 같은 직업 등록 , 회원가입 중복
    ALREADY_EXIST_JOB(HttpStatus.FORBIDDEN, "이미 존재하는 직업입니다."),
    ALREADY_EXIST_USER(HttpStatus.FORBIDDEN, "이미 회원가입된 전화번호입니다."),





    //common error
    //외부 API사용 하는 서버상의 에러의 경우
    EXTERNAL_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "주식 시세 정보 서버에 에러가 있습니다. 잠시후 다시 시도해주세요. \n 문제가 계속 될 경우 관리자에게 문의해주세요."),
    EXTERNAL_SERVER_NO_RESULT_ERROR(HttpStatus.NOT_FOUND, "종목이 존재하지 않아 정보를 불러올 수 없습니다. \n 관리자에게 문의해주세요."),



    //common error
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 에러가 발생했습니다.")

    ;


    private final HttpStatus httpStatus;

    private final String detail;




}
