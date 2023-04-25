package miraeassetmobile.backend.domain.dto.classes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassAccountResponseDto {

    Long classId;

    String classTitle;

    String classCurrency;

    int classMoney;

    String teacherName;

    int classGrade;
    int classNumber;

    String schoolName;





}
