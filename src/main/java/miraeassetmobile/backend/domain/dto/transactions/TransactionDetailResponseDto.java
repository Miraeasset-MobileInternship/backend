package miraeassetmobile.backend.domain.dto.transactions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.students.StudentInfoDto;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionDetailResponseDto {


    Long transactionId;

    int transactionMoney;

    boolean plus; // + or - 여부

    String currency; // 학급 화폐단위

    String category; //거래유형

    String depositAccount; //입금처

    String withdrawAccount; // 출금처


    StudentInfoDto managerInfo; //중개자


    String detail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY'년 'MM'월 'dd'일' HH:mm", timezone = "Asia/Seoul")
    LocalDateTime transactionDate;

    int leftMoney; //거래 후 잔고





}
