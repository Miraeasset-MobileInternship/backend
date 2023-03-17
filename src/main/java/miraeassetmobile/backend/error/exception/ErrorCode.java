package miraeassetmobile.backend.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //정상
    SUCCESS("E000", "Success"),

    //핸드폰 인증
    UNVALID_CODE("E407", "올바르지 않은 인증번호 입니다."),
    INCORRECT_CODE("E408", "올바르지 않거나 만료된 인증번호입니다. 다시 시도해주세요."),

    //로그인
    FORBIDDEN_USER("E403", "권한이 없는 사용자 입니다."),
    UNAUTHORIZED_USER("E401", "인증되지 않은 사용자 입니다."),
    INVALID_PHONENUMBER("E402","유저를 찾을 수 없습니다. 핸드폰 번호를 다시 확인해주세요."),
    ALREADY_EXIST_USER("E405", "이미 가입된 핸드폰 번호 입니다."),
    SIGN_UP_REQUIRED("E406", "가입이 되지 않은 유저입니다. 회원정보를 입력해주세요"),

    //토큰
    INVALID_SIGNATURE("E004","잘못된 JWT 서명입니다."),
    EXPIRED_TOKEN("E003","만료된 토큰 입니다."),
    UNSUPPORTED_TOKEN("E008","지원하지 않는 토큰 입니다."),
    TOKEN_NOT_EXIST("E005","존재하지 않는 토큰입니다."),
    UNAUTHORIZED_TOKEN("E007","인증되지 않은 토큰입니다."),
    INVALID_TOKEN("E009","유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN("E010", "잘못된 refreshToken 입니다."),


    //존재하지 않는 리소스
    NOT_EXIST("E401", "존재하지 않는 리소스에 대한 접근입니다."),
    TRANSACTION_DATA_NOT_EXIST("E402", "이전 거래내역이 존재하지 않습니다."),
    LOGOUT_USER("E006", "이미 로그아웃된 유저의 token정보 입니다."),


    //저장 에러
    NOT_SAVE("E802", "새로운 객체를 생성하는 과정에서 에러가 발생했습니다."),
    NOT_DELETED("E803", "객체를 삭제하는 과정에서 에러가 발생했습니다."),

    // 중복 등록
    ALREADY_EXIST_IN_CLASS("E601", "해당 유저가 이미 학급에 존재합니다."),
    ALREADY_EXIST_JOB_IN_CLASS("E602", "해당 이름의 직업이 이미 학급에 존재합니다."),
    UNAVAILABLE_ACTION_TOO_MANY_JOBS("E603", "학급에 직업을 50개 이상 등록할 수 없습니다."),
    ALREADY_EXIST_CLASS_SAME_YEAR("E604", "이미 해당 년도에 학교/학년/반 정보로 생성된 학급이 존재합니다"),
    ALREADY_EXIST_CLASS_SAME_NAME("E605", "이미 해당 학교에서 사용중인 나라이름입니다."),

    //
    UNAVAILABLE_ACTION_DELETE_JOB("E501", "삭제 불가능한 필수 직업입니다."),



    //출금
    UNAVAILABLE_ACTION_TRANSFER_MONEY("E701", "학생 잔고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_PAY_MONEY("E701", "국고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_JOB_TRANSFER("E702", "학생 잔고를 이체할 수 있는 권한이 없는 직업입니다."),
    UNAVAILABLE_ACTION_JOB_PAY("E702", "국고를 출금할 수 있는 권한이 없는 직업입니다."),



//    //로그인 (인증/인가 관련에러)
//    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "서비스를 사용할 수 없는 사용자입니다."),//403
//    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 입니다."), //401
//
//
//    //로그인 중 가입안된 사람이라 가입 처리 요청시
//    SIGN_UP_REQUIRED(HttpStatus.SEE_OTHER, "회원가입이 필요한 사용자 입니다. 회원가입 창으로 넘어가 진행 후 다시 로그인을 시도해주세요."),
//
//    // 로그인 진행 중 발생하는 필연적인 에러들 프론트에 전달하는 용도
//    FORBIDDEN_USER_LOGOUT(HttpStatus.FORBIDDEN, "이미 로그아웃한 유저입니다. 다시 로그인을 진행해주세요."),
//
//
//    //SMS인증 관련
//    MESSAGE_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "문자 전송 과정에서 에러가 발생하였습니다. 핸드폰번호를 다시한번 확인해주시고, 오류가 계속되면 관리자에게 문의해주세요."),
//
//    UNVALID_CODE(HttpStatus.UNAUTHORIZED, "올바르지 않은 인증번호 입니다."),
//    INCORRECT_CODE(HttpStatus.UNAUTHORIZED, "올바르지 않거나 만료된 인증번호입니다. 다시 시도해주세요."),
//
//
//    //권한이 없는 요청의 경우 (403경우)
//    //403 : FORBIDEN : 클라이언트는 접근 권리를 가지고 있지 않습니다. (클라이언트를 알고 있음)
//    //401 : UNAUTHORIZED : 클라이언트는 접근 권리를 가지고 있지 않습니다. (클라이언트를 모르기 때문에 접근권한이 없음)
//    UNAVAILABLE_ACTION_DELETE_JOB(HttpStatus.FORBIDDEN, "삭제 불가능한 직업입니다."),
//    UNAVAILABLE_ACTION_TRANSFER_MONEY(HttpStatus.FORBIDDEN, "학생 잔고가 부족하여 돈을 이체할 수 없습니다."),
//    UNAVAILABLE_ACTION_PAY_MONEY(HttpStatus.FORBIDDEN, "국고가 부족하여 돈을 이체할 수 없습니다."),
//    UNAVAILABLE_ACTION_JOB_TRANSFER(HttpStatus.FORBIDDEN, "학생 잔고를 이체할 수 있는 권한이 없는 직업입니다."),
//    UNAVAILABLE_ACTION_JOB_PAY(HttpStatus.FORBIDDEN, "국고를 출금할 수 있는 권한이 없는 직업입니다."),
//    UNAVAILABLE_ACTION_TOO_MANY_JOBS(HttpStatus.FORBIDDEN, "학급 당 직업은 50개를 초과할 수 없습니다."),
//
//
//    //존재하지 않는 리소스에 대한요청
//    NOT_EXIST_CLASS("E998","존재하지 않는 학급입니다."),
//    NOT_EXIST_JOB("E998","존재하지 않는 직업입니다."),
//    NOT_EXIST_STUDENT("E998","존재하지 않는 학생입니다."),
//    NOT_EXIST_USER("E998", "존재하지 않는 유저입니다."),
//    NOT_EXIST_TRANSACTION("E998", "존재하지 않거나 조회할 수 없는 거래내역입니다."),
//    NOT_EXIST_IMAGE("E998", "존재하지 않는 이미지입니다."),
//
//
//    //이미 존재하는 것을 등록하려고 할 때
//    // 같은 학급에 같은 직업 등록 , 회원가입 중복
//    ALREADY_EXIST_JOB("E997", "이미 존재하는 직업입니다."),
//    ALREADY_EXIST_USER("E997", "이미 회원가입된 전화번호입니다."),
//
//    ALREADY_EXIST_CLASS_SAME_YEAR("E997", "이미 해당 년도에 학교/학년/반 정보로 생성된 학급이 존재합니다"),
//    ALREADY_EXIST_CLASS_SAME_NAME("E997", "이미 해당 학교에서 사용중인 나라이름입니다."),
//
//
//
//
//    //common error
//    //외부 API사용 하는 서버상의 에러의 경우
//    EXTERNAL_SERVER_ERROR("E500", "주식 시세 정보 서버에 에러가 있습니다. 잠시후 다시 시도해주세요. \n 문제가 계속 될 경우 관리자에게 문의해주세요."),
//    EXTERNAL_SERVER_NO_RESULT_ERROR("E500", "종목이 존재하지 않아 정보를 불러올 수 없습니다. \n 관리자에게 문의해주세요."),
//


    //common error
    SERVER_ERROR("E500", "예상치 못한 에러가 발생했습니다."),
    MESSAGE_SERVER_ERROR("E501", "문자 전송 과정에서 에러가 발생하였습니다. 핸드폰번호를 다시한번 확인해주시고, 오류가 계속되면 관리자에게 문의해주세요."),


    ;


    private final String status;

    private final String detail;





}
