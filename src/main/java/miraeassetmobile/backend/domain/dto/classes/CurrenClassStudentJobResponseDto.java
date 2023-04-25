package miraeassetmobile.backend.domain.dto.classes;


import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;

import java.util.List;


//v2 에선 사용안함
@Builder
@Getter
public class CurrenClassStudentJobResponseDto {

    List<StudentJobDto> studentJobList;

}
