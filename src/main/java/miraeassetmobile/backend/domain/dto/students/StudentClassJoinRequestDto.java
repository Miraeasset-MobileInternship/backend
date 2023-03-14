package miraeassetmobile.backend.domain.dto.students;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentClassJoinRequestDto {

    Long userId;

    Long classId;

    int studentNumber;

}
