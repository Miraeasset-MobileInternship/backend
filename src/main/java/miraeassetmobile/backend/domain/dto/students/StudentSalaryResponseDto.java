package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentSalaryResponseDto {

    Long jobId;

    String jobTitle;

    int monthlySalary;

}
