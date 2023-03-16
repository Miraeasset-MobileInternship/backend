package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudentTransferSelectorDto {
    /*
    학생 계좌 출금/입금 시

    "1번 강미래"
    "2번 강에셋"
    SELECTOR
    프론트 편의를 고려하여 모든 방향으로 전달하기

     */


    Long studentId;

    int studentNumber;

    String studentName;






}
