package miraeassetmobile.backend.domain.dto.classes;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;

import java.util.List;

@Builder
@Getter
public class CurrenClassStudentJobResponseDto {

    List<StudentJobDto> studentJobList;

}
