package miraeassetmobile.backend.domain.dto.transactions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class StudentTransactionDataDto {

    Long transactionId; //거래 아이디

    //거래일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY.MM.dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime transactionDate;

    String category; //카테고리

    String detail; //비고

    boolean isDeposit; //입금:true / 출금:false

    int transactionMoney; //거래금액

    int studentMoney; //거래 후 잔고



}
