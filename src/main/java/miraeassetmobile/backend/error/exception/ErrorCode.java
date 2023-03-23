package miraeassetmobile.backend.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //정상
    SUCCESS("E000", "Success"),



    //인증관련
    INVALID_CODE("E101", "올바르지 않은 인증번호 입니다."),
    INCORRECT_CODE("E102", "올바르지 않거나 만료된 인증번호입니다. 다시 시도해주세요."),



    //로그인, 로그아웃 관련
    FORBIDDEN_USER("E303", "권한이 없는 사용자 입니다."),
    UNAUTHORIZED_USER("E301", "인증되지 않은 사용자 입니다."),
    INVALID_PHONE_NUMBER("E302","유저를 찾을 수 없습니다. 핸드폰 번호를 다시 확인해주세요."),
    ALREADY_EXIST_USER("E305", "이미 가입된 핸드폰 번호 입니다."),
    SIGN_UP_REQUIRED("E306", "가입이 되지 않은 유저입니다. 회원정보를 입력해주세요"),



    //토큰 관련
    INVALID_SIGNATURE("E004","잘못된 JWT 서명입니다."),
    EXPIRED_TOKEN("E003","만료된 토큰 입니다."),
    UNSUPPORTED_TOKEN("E008","지원하지 않는 토큰 입니다."),
    TOKEN_NOT_EXIST("E005","존재하지 않는 토큰입니다."),
    UNAUTHORIZED_TOKEN("E007","인증되지 않은 토큰입니다."),
    INVALID_TOKEN("E009","유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN("E010", "잘못된 refreshToken 입니다."),
    LOGOUT_USER("E006", "이미 로그아웃된 유저의 token정보 입니다."),


    //존재하지 않는 리소스에 대한 접근 (4XX)
    NOT_EXIST("E401", "존재하지 않는 리소스에 대한 접근입니다."),
    NOT_EXIST_STUDENT("E402", "존재하지 않는 학생입니다."),
    NOT_EXIST_USER("E403", "존재하지 않는 유저입니다."),
    NOT_EXIST_CLASS("E404", "존재하지 않는 학급입니다."),
    NOT_EXIST_JOB("E405", "존재하지 않는 직업입니다."),
    NOT_EXIST_STOCK_DATA("E406", "존재하지 않는 주식정보에 대한 접근이 포함되어 있습니다."),
    NOT_EXIST_TRANSACTION_DATA("E407", "거래내역이 존재하지 않습니다."),
    NOT_EXIST_STOCK_TRADING_DATA("E408", "존재하지 않는 주식거래에 대한 접근이 포함되어 있습니다."),
    NOT_EXIST_IMAGE("E409", "존재하지 않는 이미지입니다."),
    NOT_EXIST_CATEGORY("E410", "존재하지 않는 카테고리입니다."),
    NOT_EXIST_CATEGORY_TYPE("E411", "존재하지 않는 카테고리 타입(transfer/pay)에 대한 요청 입니다."),



    //저장,삭제, 업데이트 에러 (8XX)
    NOT_SAVE("E802", "새로운 정보를 등록하는 과정에서 에러가 발생했습니다."),
    NOT_SAVE_CLASS("E803", "새로운 학급 등록 과정에서 에러가 발생했습니다. 잠시후에 다시 시도해주세요."),
    NOT_SAVE_JOB("E804", "새로운 직업 등록 과정에서 에러가 발생했습니다. 잠시후에 다시 시도해주세요."),
    NOT_SAVE_TRANSFER("E805", "이체 과정에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요."),
    NOT_SAVE_PAY("E806", "지급 과정에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요."),
    NOT_SAVE_CODE("E807", "인증 코드를 생성하는 과정에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요."),
    NOT_SAVE_JOIN_CLASS("E808", "학생을 학급에 가입시키는 과정에서 에러가 발생하였습니다. 잠시후 다시 시도해주세요."),

    NOT_SAVE_LOGIN("E809", "로그인 도중 서버에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요."),
    NOT_SAVE_SIGNUP("E810", "회원가입 과정에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요."),

    NOT_DELETED("E820", "정보를 삭제하는 과정에서 에러가 발생했습니다."),
    NOT_UPDATED("E830", "정보를 수정하는 과정에서 에러가 발생했습니다."),

    LOGOUT_ERROR("E840", "로그아웃 과정에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요."),
    TOKEN_REISSUE_ERROR("E850", "재인증 과정에서 에러가 발생하였습니다. 다시 로그인을 시도해주세요."),



    // 중복 등록
    ALREADY_EXIST_IN_CLASS("E601", "해당 유저가 이미 학급에 존재합니다."),
    ALREADY_EXIST_JOB_IN_CLASS("E602", "해당 이름의 직업이 이미 학급에 존재합니다."),
    UNAVAILABLE_ACTION_TOO_MANY_JOBS("E603", "학급에 직업을 50개 이상 등록할 수 없습니다."),
    ALREADY_EXIST_CLASS_SAME_YEAR("E604", "이미 해당 년도에 학교/학년/반 정보로 생성된 학급이 존재합니다"),
    ALREADY_EXIST_CLASS_SAME_NAME("E605", "이미 해당 학교에서 사용중인 나라이름입니다."),




    // 불가능한 수행 내역
    UNAVAILABLE_ACTION_DELETE_JOB("E701", "삭제 불가능한 필수 직업입니다."),

    //출금
    UNAVAILABLE_ACTION_TRANSFER_MONEY("E702", "학생 잔고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_PAY_MONEY("E703", "국고가 부족하여 돈을 이체할 수 없습니다."),
    UNAVAILABLE_ACTION_JOB_TRANSFER("E704", "학생 잔고를 이체할 수 있는 권한이 없는 직업입니다."),
    UNAVAILABLE_ACTION_JOB_PAY("E705", "국고를 출금할 수 있는 권한이 없는 직업입니다."),

    UNAVAILABLE_ACTION_TRANSFER_TAG("E706", "이체하기 기능에서 사용할 수 없는 태그입니다."),
    UNAVAILABLE_ACTION_PAY_TAG("E707", "지급하기 기능에서 사용할 수 없는 태그입니다."),
    UNAVAILABLE_ACTION_TRANSFER_ZERO("E708", "0원을 이체/지급할 수 없습니다."),
    UNAVAILABLE_ACTION_NOT_INCLUDED_STUDENT("E709", "해당 학급에 학생이 아닙니다."),


    //주식
    NOT_OWNED_STOCK("E901", "보유하지 않은 주식 종목입니다."),
    NOT_ENOUGH_SHARES("E902","판매하려는 수량이 보유하고 있는 수량보다 많습니다."),
    NOT_ENOUGH_MONEY_BUYING("E903", "주문가능한 금액이 부족합니다."),

    TRADING_STOCK("E950", "주식 체결과정에서 에러가 발생하였습니다. 잠시후에 다시 시도해주세요"),


    //common error (서버에러 5xx)
    SERVER_ERROR("E500", "예상치 못한 서버 에러가 발생했습니다."),
    MESSAGE_SERVER_ERROR("E501", "문자 전송 과정에서 에러가 발생하였습니다. 핸드폰번호를 다시한번 확인해주시고, 오류가 계속되면 관리자에게 문의해주세요."),
    API_SEVER_ERROR_NAVER("E502", "영문 번역과정에서 외부 서버 에러가 발생하였습니다. 오류가 계속되면 관리자에게 문의해주세요."),
    API_SEVER_ERROR_YHFINANCE("E503", "주식 시세정보를 가져오는 과정에서 외부 서버 에러가 발생하였습니다. 오류가 계속되면 관리자에게 문의해주세요."),
    API_SEVER_ERROR_RAPID_YHFINANCE("E504", "기사를 가져오는 과정에서 외부 서버 에러가 발생하였습니다. 오류가 계속되면 관리자에게 문의해주세요."),

    ;


    private final String status;

    private final String detail;





}


