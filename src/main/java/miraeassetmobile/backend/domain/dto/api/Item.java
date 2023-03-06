package miraeassetmobile.backend.domain.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {


    private String basDt; // 기준일자 v

    private String srtnCd; //단축코드 v


    private String isinCd; //ISIN 코드 v


    private String itmsNm; //종목명 v

    private String mrktCtg; //시장구분 v

    private String clpr; //정규시장 종가 v

    private String vs; // 전일 대비 등락 v


    private String fltRt; // 전일대비  v


    private String mkp; //정규시장의 최초가격 v


    private String hipr; // 하루중 가격의 최고치 v


    private String lopr; //하루중 가격의 최저치 v


    private String trqu; // 체결수량의 누적합계 (거래량) v


    private String trPrc; //거래 대금 (거래건 별 체결가격 * 체결 수량 누적 합계) v

    private String mrktTotAmt; // v

    private String lstgStCnt; // v


//    private String lstgCtfCnt; // 상장 증서수 : 신주인수권증서의 상장증서수


//    private String nstlssPrc; // 신주발행가 : 신주인수권증서의 신주발행가


//    private String dltDt; //상장폐지일 : 신주인수권증서의 상장폐지일


//    private String purRgtScrtltmsCd; //목적주권 종목코드


//    private String purRgtScrtltmsNm; //목적주권 종목명


//    private String getPurRgtScrtltmsClpr; // 목적주권 종가




}
