package miraeassetmobile.backend.domain.dto.students;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentClassJoinRequestDto {

    Long userId;

    Long classId;

    int studentNumber;

}
