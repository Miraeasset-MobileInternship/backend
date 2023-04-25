package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentAccountResponseDto {

    Long studentId; //누구인지
    int money;//보유 돈

    String currency; //학급 화폐 단위

    int creditScore; //신용점수



    String teacherName;

    int classGrade;
    int classNumber;
    int studentNumber;

    String schoolName;
}
