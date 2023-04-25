package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentJobResponseDto {

    Long studentId;

    String classInfo;
    String profileImg;

    Long jobId;

    String JobTitle;

    String JobDetail;

    boolean isTransfer; // 이체하기(학생 계좌 출금 기능)

    boolean isPay;// 지급하기(국고 출금 기능)


}
