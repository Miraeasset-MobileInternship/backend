package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentInfoDto {

    Long studentId;

    String studentName;

    String studentJob;

    int studentNumber;


}
