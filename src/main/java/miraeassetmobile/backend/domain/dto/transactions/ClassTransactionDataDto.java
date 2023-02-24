package miraeassetmobile.backend.domain.dto.transactions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;

import java.time.LocalDate;

@Getter
@Builder
public class ClassTransactionDataDto {

    Long transactionId; //거래 아이디

    //거래일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private LocalDate transactionDate;

    String category; //카테고리

    String detail; //비고

    boolean isDeposit; //입금:true / 출금:false

    int transactionMoney; //거래금액


    //담당자 와 입금자

    StudentJobDto manager; //거래 담당자

    StudentJobDto student;// 거래 대상자(?)



}
